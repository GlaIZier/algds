package ru.glaizier.algds.ds.functional.lazylist;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.Getter;

/**
 * @author GlaIZier
 */
public class LazyDoubleLinkedList<T> extends LazyList<T> {

    @Getter
    private final LazyList<T> prev;


    public LazyDoubleLinkedList(T value, Supplier<? extends LazyList<T>> next, LazyDoubleLinkedList<T> prev) {
        super(value, next);
        this.prev = prev;
    }

    public LazyDoubleLinkedList<T> next() {
        LazyList<T> lazyList = next.get();
        if (!(lazyList instanceof LazyDoubleLinkedList))
            throw new IllegalArgumentException("Unexpected class in LazyDoubleLinkedList: " + lazyList.getClass().getName());
        return (LazyDoubleLinkedList<T>) lazyList;
    }

    public void setNext(Supplier<? extends LazyList<T>> next) {
        this.next = next;
    }

    public static class Factory<T> extends LazyList.Factory<T> {

        public Factory(UnaryOperator<T> transformer) {
            super(transformer);
        }

        public LazyDoubleLinkedList<T> from(T seed) {
            return from(seed, null);
        }

        private LazyDoubleLinkedList<T> from(T seed, LazyDoubleLinkedList<T> prev) {
            LazyDoubleLinkedList<T> created = new LazyDoubleLinkedList<>(seed, null, prev);
            // To be able to link the previous node with created, we need to avoid catch-22 problem.
            // So, we need a setter for the next function. Otherwise, we cannot created list as the prev one.
            created.setNext(() -> from(transformer.apply(seed), created));
            return created;
        }
    }

}
