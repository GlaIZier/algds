package ru.glaizier.algds.ds.concurrent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

public class CopyOnWriteArrayList<T> {

    private volatile ArrayList<T> array = new ArrayList<T>();

    private final AtomicLong version = new AtomicLong();

    public void add(T t) {
        while (true) {
            long prevVersion = this.version.get();
            long newVersion = prevVersion++;

            ArrayList<T> newArray = new ArrayList<>();
            newArray.addAll(array);
            newArray.add(t);
            if (version.compareAndSet(prevVersion, newVersion)) {
                array = newArray;
                return;
            }
        }
    }

}
