package ru.glaizier.algds.ds.lazylist;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author GlaIZier
 */
@AllArgsConstructor
public class LazyList<T> {
    @Getter
    private final T value;

    // This class is immutable, but, to support DoubleLinkedList, I made it mutable for children
    protected Supplier<? extends LazyList<T>> next;

    public LazyList<T> next() {
        return next.get();
    }

    @RequiredArgsConstructor
    public static class Factory<T> {
        protected final UnaryOperator<T> transformer;

        public LazyList<T> from(T seed) {
            return new LazyList<>(seed, () -> from(transformer.apply(seed)));
        }
    }

}
