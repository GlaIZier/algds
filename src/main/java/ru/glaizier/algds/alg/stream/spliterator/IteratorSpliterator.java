package ru.glaizier.algds.alg.stream.spliterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IteratorSpliterator<T> implements Spliterator<T> {

    private static final int DEFAULT_BATCH_SIZE = 1 << 13;

    private static final int SPLIT_COEFFICIENT = 2;

    private final Collection<T> collection;

    private final Iterator<T> iterator;

    private final int characteristics;

    private final int batchSize;

    private long estimateSize;

    public IteratorSpliterator(Collection<T> collection, int characteristics, int batchSize) {
        Objects.requireNonNull(collection);
        this.collection = collection;
        this.iterator = collection.iterator();
        this.estimateSize = (long) collection.size();
        this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
            ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
            : characteristics;
        this.batchSize = batchSize;
    }

    public IteratorSpliterator(Collection<T> collection, int characteristics) {
        this(collection, characteristics, DEFAULT_BATCH_SIZE);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (iterator.hasNext()) {
            action.accept(iterator.next());
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        iterator.forEachRemaining(action);
    }

    // Awful implementation. We could use here an array implementation with a fence to avoid copying, but this is used just
    // for this pet project and is not fussed about the effectiveness
    @Override
    public Spliterator<T> trySplit() {
        if (estimateSize <= batchSize)
            return null;
        long newSpliteratorSize = estimateSize / SPLIT_COEFFICIENT;
        List<T> list = new ArrayList<>(Math.toIntExact(newSpliteratorSize));
        for (int i = 0; i < newSpliteratorSize && iterator.hasNext(); i++)
            list.add(iterator.next());
        estimateSize -= list.size();
        return new IteratorSpliterator<>(list, characteristics);
    }

    @Override
    public long estimateSize() {
        return estimateSize;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }

}
