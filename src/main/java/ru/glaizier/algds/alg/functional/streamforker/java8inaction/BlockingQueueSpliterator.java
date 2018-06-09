package ru.glaizier.algds.alg.functional.streamforker.java8inaction;

import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import static ru.glaizier.algds.alg.functional.streamforker.java8inaction.StreamForkConsumer.FENCE;

/**
 * @author GlaIZier
 */
public class BlockingQueueSpliterator<T> implements Spliterator<T> {

    private final BlockingQueue<T> queue;

    public BlockingQueueSpliterator(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        T element;
        while (true) {
            try {
                element = queue.take();
                break;
            } catch (InterruptedException e) {
                System.out.println("tryAdvanced() has been interrupted. Continue waiting...");
            }
        }
        if (element == FENCE) {
            return false;
        }
        action.accept(element);
        return true;
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
