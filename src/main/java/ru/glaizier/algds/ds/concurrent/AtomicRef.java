package ru.glaizier.algds.ds.concurrent;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

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

    public static void main(String[] args) {
        System.out.println("valueOffset = " + valueOffset);
    }

}
