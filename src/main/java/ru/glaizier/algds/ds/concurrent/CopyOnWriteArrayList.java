package ru.glaizier.algds.ds.concurrent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class CopyOnWriteArrayList<T> {

    private volatile ArrayList<T> array = new ArrayList<>();

    private final AtomicLong version = new AtomicLong();

    public void add(T elem) {
        compareAndSwapArray(array -> {
            array.add(elem);
            return null;
        });
    }

    public void add(int index, T elem) {
        compareAndSwapArray(array -> {
            array.add(index, elem);
            return null;
        });
    }

    public T set(int index, T elem) {
        return compareAndSwapArray(array -> array.set(index, elem));
    }

    public T remove(int index) {
        return compareAndSwapArray(array -> array.remove(index));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean remove(Object elem) {
        return compareAndSwapArray(array -> array.remove(elem));
    }

    public T get(int index) {
        return array.get(index);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object elem) {
        return array.contains(elem);
    }

    public int size() {
        return array.size();
    }

    private <R> R compareAndSwapArray(Function<? super ArrayList<T>, ? extends R> arrayTransformer) {
        while (true) {
            long prevVersion = this.version.get();
            long newVersion = prevVersion + 1;

            ArrayList<T> newArray = new ArrayList<>(array);
            R result = arrayTransformer.apply(newArray);

            if (version.compareAndSet(prevVersion, newVersion)) {
                array = newArray;
                return result;
            }
        }
    }

}
