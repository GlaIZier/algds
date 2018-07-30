package ru.glaizier.algds.ds.concurrent;

import java.lang.reflect.Field;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import sun.misc.Unsafe;

public class AtomicRef<V> {
    private static final Unsafe unsafe = getUnsafe();

    private static final long valueOffset;

    private volatile V value;

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (AtomicRef.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public AtomicRef(V initialValue) {
        value = initialValue;
    }

    public AtomicRef() {}

    public V get() {
        return value;
    }

    public void set(V newValue) {
        this.value = newValue;
    }

    public boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    @SuppressWarnings("unchecked")
    public V getAndSet(V newValue) {
        return (V) unsafe.getAndSetObject(this, valueOffset, newValue);
    }

    public V getAndUpdate(UnaryOperator<V> transformer) {
        V prev, next;
        do {
            prev = get();
            next = transformer.apply(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public V getAndAccumulate(V addition, BinaryOperator<V> accumulator) {
        V prev, next;
        do {
            prev = get();
            next = accumulator.apply(prev, addition);
        } while (!compareAndSet(prev, next));
        return prev;
    }

}
