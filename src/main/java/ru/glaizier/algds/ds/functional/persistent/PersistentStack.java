package ru.glaizier.algds.ds.functional.persistent;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Value;

public class PersistentStack<T> {

    private final Node<T> fenceNode = new Node<>(null, null);

    private AtomicReference<Node<T>> head = new AtomicReference<>(fenceNode);

    @Value
    private static class Node<T> {
        T value;
        Node<T> next;
    }

    public void push(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        head.getAndUpdate(prevHead -> new Node<>(value, prevHead));
    }


    /**
     * @param value
     * @param index if index is greater than the number of elements, than it will be put the last
     */
    public void put(T value, int index) {

    }

    public Optional<T> pop() {
        if (head.get() == fenceNode)
            return empty();

        return of(head.getAndUpdate(Node::getNext).getValue());
    }

    public Optional<T> peek() {
        if (head.get() == fenceNode)
            return empty();

        return of(head.get().getValue());
    }

    public Optional<T> get(T value) {
        Node<T> cur = head.get();
        while (cur != fenceNode && !value.equals(cur)) {
            cur = cur.getNext();
        }
        return ofNullable(cur.getValue());
    }

    public Optional<T> get(int index) {
        if (index < 0)
            throw new IllegalArgumentException();
//        int i = 0;
//        while (cur != fenceNode) {
//            if (i == index)
//                return of(cur.getValue());
//            cur = cur.getNext();
//            i++;
//        }
        Node<T> cur = head.get();
        for(int i = 0; i <= index && cur != fenceNode; i++, cur = cur.getNext()) {
            if (i == index)
                return of(cur.getValue());
        }
        return empty();
    }

    private Node<T> getAndSet(Node<T> newHead) {
        while (true) {
            Node<T> prev = head.get();
            if (head.compareAndSet(prev, newHead))
                return prev;
        }
    }

    private Node<T> find(T value) {
        Node<T> cur = head.get();
        while (cur != null && !value.equals(cur)) {
            cur = cur.getNext();
        }
        return cur;
    }

}
