package ru.glaizier.algds.alg.functional.streamforker.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author GlaIZier
 */
public class SimpleStreamForker<T> {

    private final Stream<T> stream;

    private final Map<Object, Function<Stream<T>, ?>> operations = new HashMap<>();

    public SimpleStreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public SimpleStreamForker<T> fork(Object key, Function<Stream<T>, ?> operation) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(operation);
        operations.put(key, operation);
        return this;
    }

    public SimpleForkResult getResult() {
        return new SimpleStreamForkConsumer<>(stream, operations);
    }
}
