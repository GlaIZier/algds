package ru.glaizier.algds.alg.functional.streamforker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author GlaIZier
 */
public class StreamForker<T> {

    private final Stream<T> stream;

    private final Map<String, Function<Stream<T>, ?>> operations = new HashMap<>();

    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamForker<T> fork(String key, Function<Stream<T>, ?> operation) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(operation);
        operations.put(key, operation);
        return this;
    }

    public ForkResult getResult(String key) {
        return null;
    }
}
