package ru.glaizier.algds.ds.lazylist;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author GlaIZier
 */
@RequiredArgsConstructor
public class LazyList<T> {
    @Getter
    private final T value;
    private final Supplier<LazyList<T>> next;

    public LazyList<T> next() {
        return next.get();
    }

    @RequiredArgsConstructor
    public static class Factory<T> {
        private final UnaryOperator<T> transformer;

        public LazyList<T> from(T seed) {
            return new LazyList<>(seed, () -> from(transformer.apply(seed)));
        }
    }

    public static void main(String[] args) {
        Factory<Integer> factory = new Factory<>(i -> i + 1);

        System.out.println(factory.from(0).getValue());
        System.out.println(factory.from(0).next().next().getValue());
    }
}
