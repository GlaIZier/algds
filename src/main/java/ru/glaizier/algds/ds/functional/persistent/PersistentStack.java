package ru.glaizier.algds.ds.functional.persistent;

import io.vavr.collection.List;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.empty;
import static java.util.Optional.of;

// Todo to support null values
public class PersistentStack<T> {

    private static final PersistentStack<?> FENCE = new PersistentStack<>(null, null);

    private final T value;

    private final PersistentStack<T> next;

    @SuppressWarnings("unchecked")
    private static <T> PersistentStack<T> fence() {
        return (PersistentStack<T>) FENCE;
    }

    public PersistentStack(T value, PersistentStack<T> next) {
        this.value = value;
        this.next = next;
    }


    public PersistentStack<T> push(T value) {
        return new PersistentStack<T>(value, this);
    }


    // Todo add update() with usage of put()
    /**
     * @param value
     * @param index if index is greater than the number of elements, than it will be put the last
     */
    public PersistentStack<T> put(T value, int index) {
        Objects.requireNonNull(value);
        if (index < 0)
            throw new IllegalArgumentException();
        // Todo remove it?
//        if (index == 0)
//            return push(value);

        PersistentStack<T> backwardHead = fence();
        PersistentStack<T> cur = this;
        int i = 0;
        // get the the beginning of the result backwards
        for (; i < index && cur != fence(); i++, cur = cur.next)
            backwardHead = backwardHead.push(cur.value);

        // add the new element
        backwardHead = backwardHead.push(value);

        // copy the result by turning arounf
        PersistentStack<T> result = cur;
        for(; backwardHead != fence(); backwardHead = backwardHead.next)
            result = result.push(backwardHead.value);

        return result;
    }

    public Optional<Entry<T, PersistentStack<T>>> pop() {
        return (this == fence()) ?
                empty() :
                of(new SimpleImmutableEntry<>(value, next));
    }

    public Optional<T> peek() {
        return (this == fence()) ?
                empty() :
                of(value);
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value);

        PersistentStack<T> cur = this;
        while (cur != fence() && !value.equals(cur.value))
            cur = cur.next;
        return cur != fence();
    }

    public Optional<T> get(int index) {
        if (index < 0)
            throw new IllegalArgumentException();

        PersistentStack<T> cur = this;
        int i = 0;
        while (cur != fence() && i < index) {
            cur = cur.next;
            i++;
        }
        return (cur == fence()) ?
                empty() :
                of(cur.value);
    }

    public void forEach(Consumer<? super T> action) {
        for(PersistentStack<T> cur = this; cur != fence(); cur = cur.next)
            action.accept(cur.value);
    }

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3);
    }

}
