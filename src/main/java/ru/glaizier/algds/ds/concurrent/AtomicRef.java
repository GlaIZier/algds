package ru.glaizier.algds.ds.concurrent;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicRef<V> {

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (AtomicReference.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile V value;

    public AtomicRef(V initialValue) {
        value = initialValue;
    }

    public AtomicRef() {
    }
}
