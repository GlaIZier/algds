package ru.glaizier.algds.ds.lazylist;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author GlaIZier
 */
@RequiredArgsConstructor
public class LazyList<T> {
    @Getter
    private final T value;
    @Getter
    private final LazyList<T> prev;
    @Setter
    private Supplier<LazyList<T>> next;

    public LazyList<T> next() {
        return next.get();
    }

    @RequiredArgsConstructor
    public static class Factory<T> {
        private final UnaryOperator<T> transformer;

        public LazyList<T> from(T seed) {
            return from(seed, null);
        }

        private LazyList<T> from(T seed, LazyList<T> prev) {
            LazyList<T> created = new LazyList<>(seed, prev);
            // To be able to link this to the previous node with created, we need to avoid catch-22 problem.
            // So, we need a setter for the next function. Otherwise, we cannot point at created list as the prev one.
            created.setNext(() -> from(transformer.apply(seed), created));
            return created;
        }
    }

    public static void main(String[] args) {
        Factory<Integer> factory = new Factory<>(i -> i + 1);

        System.out.println(factory.from(0).getValue());
        System.out.println(factory.from(0).next().next().getValue());
        System.out.println(factory.from(0).next().next().getPrev().getValue());
    }
}
