package ru.glaizier.algds.ds.functional.persistent;

import io.vavr.collection.List;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class PersistentStack<T> {

    private static final PersistentStack FENCE = new PersistentStack<>(null, null);

    private final T value;

    private final PersistentStack<T> next;

    public PersistentStack(T value, PersistentStack<T> next) {
        this.value = value;
        this.next = next;
    }


    public PersistentStack<T> push(T value) {
        return null;
    }


    /**
     * @param value
     * @param index if index is greater than the number of elements, than it will be put the last
     */
    public PersistentStack<T> put(T value, int index) {
        return null;
    }

    public Optional<Entry<T, PersistentStack<T>>> pop() {
        return (this == FENCE) ?
                empty() :
                of(new SimpleImmutableEntry<>(value, next));
    }

    public Optional<T> peek() {
        return (this == FENCE) ?
                empty() :
                of(value);
    }

    public Optional<T> get(T value) {
        return null;
    }

    public Optional<T> get(int index) {
        return null;
    }

    public void forEach(Consumer<? super T> action) {
//        for(Node<T> cur = head.get(); cur != FENCE; cur = cur.getNext())
//            action.accept(cur.getValue());
    }

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3);
    }

}
