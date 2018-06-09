package ru.glaizier.algds.alg.functional.streamforker.java8inaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import ru.glaizier.algds.alg.functional.streamforker.ForkResult;
import ru.glaizier.algds.alg.functional.streamforker.Forker;

/**
 * @author GlaIZier
 */
public class StreamForker<T> implements Forker<T> {

    private final Stream<T> stream;

    private final Map<Object, Function<Stream<T>, ?>> operations = new HashMap<>();

    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamForker<T> fork(Object key, Function<Stream<T>, ?> operation) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(operation);
        operations.put(key, operation);
        return this;
    }

    public ForkResult getResult() {
        StreamForkConsumer<T> consumer = new StreamForkConsumer<>(operations);
        try {
            stream.sequential().forEach(consumer);
        } finally {
            consumer.finish();
        }
        return consumer;
    }
}
