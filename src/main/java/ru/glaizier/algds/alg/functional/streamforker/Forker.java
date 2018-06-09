package ru.glaizier.algds.alg.functional.streamforker;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author GlaIZier
 */
public interface Forker<T> {
    Forker<T> fork(Object key, Function<Stream<T>, ?> operation);
    ForkResult getResult();
}
