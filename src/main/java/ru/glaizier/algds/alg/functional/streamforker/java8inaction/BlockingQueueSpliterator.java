package ru.glaizier.algds.alg.functional.streamforker.java8inaction;

import java.util.Spliterator;
import java.util.concurrent.BlockingDeque;
import java.util.function.Consumer;

/**
 * @author GlaIZier
 */
public class BlockingQueueSpliterator<T> implements Spliterator<T> {

    public <T> BlockingQueueSpliterator(BlockingDeque<T> queue) {
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
