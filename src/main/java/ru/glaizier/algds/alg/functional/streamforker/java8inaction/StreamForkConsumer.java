package ru.glaizier.algds.alg.functional.streamforker.java8inaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author GlaIZier
 */
public class StreamForkConsumer<T> implements ForkResult, Consumer<T> {

    static Object FENCE = new Object();

    List<BlockingQueue<T>> queues = new ArrayList<>();

    private Map<Object, Function<Stream<T>, ?>> operations = new HashMap<>();

    private final Map<Object, Future<?>> results;

    public StreamForkConsumer(Map<Object, Function<Stream<T>, ?>> operations) {
        results = operations.entrySet().stream().reduce(
            new HashMap<Object, Future<?>>(),
            (map, e) -> {
                map.put(e.getKey(), getOperationResult(e.getValue()));
                return map;
            },
            (m1, m2) -> {
                m1.putAll(m2);
                return m1;
            }
        );
        this.operations = operations;
    }

    // This method immediately runs the async task. But this task will get stuck because of the lack of elements in the
    // BlockingQueue. See tryAdvance() in Spliterator which calls take() method that blocks the execution until the
    // the element is present
    private Future<?> getOperationResult(Function<Stream<T>, ?> operation) {
        LinkedBlockingDeque<T> queue = new LinkedBlockingDeque<>();
        queues.add(queue);
        Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
        Stream<T> stream = StreamSupport.stream(spliterator, false);
        return CompletableFuture.supplyAsync(() -> operation.apply(stream));
    }

    @Override
    public void accept(T t) {
        queues.forEach(queue -> queue.add(t));
    }

    @SuppressWarnings("unchecked")
    public void finish() {
       accept((T) FENCE);
    }

    @Override
    public <R> R get(Object key) {
        new StreamForkConsumer<>(operations);
        try {
            return  (R) results.get(key).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
