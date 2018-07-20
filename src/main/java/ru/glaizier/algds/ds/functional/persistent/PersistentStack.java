package ru.glaizier.algds.ds.functional.persistent;

import io.vavr.collection.List;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import static java.util.Optional.of;

// Todo to support null values
public class PersistentStack<T> {

    private static final PersistentStack<?> FENCE = new PersistentStack<>(null, null);

    private final T value;

    private final PersistentStack<T> next;

    @SuppressWarnings("unchecked")
    public static <T> PersistentStack<T> empty() {
        return (PersistentStack<T>) FENCE;
    }

    private PersistentStack(T value, PersistentStack<T> next) {
        this.value = value;
        this.next = next;
    }


    public PersistentStack<T> push(T value) {
        return new PersistentStack<T>(value, this);
    }

    /**
     * @param value
     * @param index if index is greater than the number of elements, than it will be add the last
     */
    public PersistentStack<T> add(int index, T value) {
        return update(index, value, (backwardHead, tail) -> {
            // add the new element
            backwardHead = backwardHead.push(value);
            return merge(backwardHead, tail);
        });
    }

    public PersistentStack<T> update(int index, T value) {
        return update(index, value, (backwardHead, tail) -> {
            // add the new element
            backwardHead = backwardHead.push(value);
            // skip the updated element
            tail = tail.next;
            return merge(backwardHead, tail);
        });
    }

    public Optional<Entry<T, PersistentStack<T>>> pop() {
        return (this == empty()) ?
                Optional.empty() :
                of(new SimpleImmutableEntry<>(value, next));
    }

    public Optional<T> peek() {
        return (this == empty()) ?
                Optional.empty() :
                of(value);
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value);

        PersistentStack<T> cur = this;
        while (cur != empty() && !value.equals(cur.value))
            cur = cur.next;
        return cur != empty();
    }

    public Optional<T> get(int index) {
        if (index < 0)
            throw new IllegalArgumentException();

        PersistentStack<T> cur = this;
        int i = 0;
        while (cur != empty() && i < index) {
            cur = cur.next;
            i++;
        }
        return (cur == empty()) ?
                Optional.empty() :
                of(cur.value);
    }

    public void forEach(Consumer<? super T> action) {
        for (PersistentStack<T> cur = this; cur != empty(); cur = cur.next)
            action.accept(cur.value);
    }

    private PersistentStack<T> update(int index, T value, BinaryOperator<PersistentStack<T>> merger) {
        Objects.requireNonNull(value);
        if (index < 0)
            throw new IllegalArgumentException();
        // Todo remove it?
//        if (index == 0)
//            return push(value);

        PersistentStack<T> backwardHead = empty();
        PersistentStack<T> cur = this;
        int i = 0;
        // get the the beginning of the result backwards
        for (; i < index && cur != empty(); i++, cur = cur.next)
            backwardHead = backwardHead.push(cur.value);

        return merger.apply(backwardHead, cur);
    }

    private PersistentStack<T> merge(PersistentStack<T> backwardHead, PersistentStack<T> tail) {
        // copy the result by turning around backwardHead
        PersistentStack<T> result = tail;
        for (; backwardHead != empty(); backwardHead = backwardHead.next)
            result = result.push(backwardHead.value);

        return result;
    }

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3);
    }

}
