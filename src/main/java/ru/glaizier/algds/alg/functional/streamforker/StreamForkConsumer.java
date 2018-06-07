package ru.glaizier.algds.alg.functional.streamforker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author GlaIZier
 */
public class StreamForkConsumer<T> implements ForkResult, Consumer<T> {

    private List<T> elements = new ArrayList<>();

    private Map<Object, Function<Stream<T>, ?>> operations = new HashMap<>();

    // Concurrent HashMap?
    private Map<Object, Future<?>> results = new HashMap<>();

    public StreamForkConsumer(Stream<T> stream, Map<Object, Function<Stream<T>, ?>> operations) {
        this.operations = operations;
        stream.sequential().forEach(this);
        finish();
    }

    @Override
    public void accept(T t) {
        elements.add(t);
    }

    private void finish() {
        operations.forEach((o, streamFunction) -> {
            Function<Stream<T>, ?> operation = operations.get(o);
            CompletableFuture<?> result = CompletableFuture.supplyAsync(() -> operation.apply(elements.stream()));
            results.put(o, result);
        });
    }

    @Override
    public <R> R get(Object key) {
        try {
            return  (R) results.get(key).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
