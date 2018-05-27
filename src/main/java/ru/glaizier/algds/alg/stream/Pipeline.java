package ru.glaizier.algds.alg.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Pipeline<T> implements Stream<T> {

    private final Spliterator<T> spliterator;

    private static class Operation<IN, OUT> {
//        OUT apply(IN in) {
//
//        }
    }

    public Pipeline(Spliterator<T> spliterator) {
        this.spliterator = spliterator;
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {

    }
}
