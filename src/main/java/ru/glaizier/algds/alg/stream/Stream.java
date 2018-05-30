package ru.glaizier.algds.alg.stream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface Stream<T> {

    /**
     * Doesn't guarantee the order
     */
    Stream<T> parallel();

    Stream<T> filter(Predicate<? super T> predicate);

    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    void forEach(Consumer<? super T> action);

    <R, A> R collect (Collector<? super T, A, ? extends R> collector);

}
