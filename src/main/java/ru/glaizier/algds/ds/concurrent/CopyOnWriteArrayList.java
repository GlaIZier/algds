package ru.glaizier.algds.ds.concurrent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

public class CopyOnWriteArrayList<T> {

    private AtomicReference<ArrayList<T>> array = new AtomicReference<>(new ArrayList<>());

    public void add(T elem) {
        array.getAndUpdate(prevArray -> {
            ArrayList<T> newArray = new ArrayList<>(prevArray);
            newArray.add(elem);
            return newArray;
        });
    }

    public void add(int index, T elem) {
        array.getAndUpdate(prevArray -> {
            ArrayList<T> newArray = new ArrayList<>(prevArray);
            newArray.add(index, elem);
            return newArray;
        });
    }

    public T set(int index, T elem) {
        return compareAndSwap(array -> array.set(index, elem));
    }

    public T remove(int index) {
        return compareAndSwap(array -> array.remove(index));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean remove(Object elem) {
        return compareAndSwap(array -> array.remove(elem));
    }

    public T get(int index) {
        return array.get().get(index);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object elem) {
        return array.get().contains(elem);
    }

    public int size() {
        return array.get().size();
    }

    // stream() takes some current state
    public Stream<T> stream() {
        return array.get().stream();
    }

    // We can do it with getAndUpdate() but this is for better understanding how it works
    private <R> R compareAndSwap(Function<? super ArrayList<T>, ? extends R> arrayTransformer) {
        while (true) {
            ArrayList<T> prevArray = this.array.get();

            ArrayList<T> newArray = new ArrayList<>(this.array.get());
            R result = arrayTransformer.apply(newArray);

            if (array.compareAndSet(prevArray, newArray))
                return result;
        }
    }

}
