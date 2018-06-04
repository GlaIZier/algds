package ru.glaizier.algds.ds.functional.lazylist;

import java.util.function.Predicate;
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

    /**
     * This method searches for the next element that satisfies the provided predicate by checking the current value and
     * transforming the next function to make next elements be filtered by this predicate too.
     *
     * Assuming we have such simple sequence i (n) = i (n - 1) + 1.
     * at first we filter this sequence by providing n % 2 != 0 and get our first element in a new filtered sequence
     * (All other next elements will be filtered with n % 2 != 0 too)
     * then we filter the result sequence with n % 3 !=0 and get our second element in a sequence. So, all next elements
     * will be filtered with two predicates. And so forth...
     *
     * So, the filter function works as a nesting doll
     * @param p
     * @return
     */
    public LazyList<T> filter(Predicate<T> p) {
        return p.test(value) ?
            new LazyList<>(value, () -> next().filter(p)) :
            next().filter(p);
    }

    @RequiredArgsConstructor
    public static class Factory<T> {
        protected final UnaryOperator<T> transformer;

        public LazyList<T> from(T seed) {
            return new LazyList<>(seed, () -> from(transformer.apply(seed)));
        }
    }

}
