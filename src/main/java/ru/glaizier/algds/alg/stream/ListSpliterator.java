package ru.glaizier.algds.alg.stream;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ListSpliterator<T> implements Spliterator<T> {

    private final List<T> list;

    public ListSpliterator(List<T> list) {
        this.list = list;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return 0;
    }

    @Override
    public int characteristics() {
        return 0;
    }
}
