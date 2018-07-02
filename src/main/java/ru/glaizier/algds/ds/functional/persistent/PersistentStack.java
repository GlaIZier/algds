package ru.glaizier.algds.ds.functional.persistent;

import static java.util.Optional.empty;
import static java.util.Optional.of;
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

        head.getAndSet(new Node<>(value, head.get()));
    }

    public Optional<T> pop() {
        if (head.get() == fenceNode)
            return empty();

        return of(head.getAndSet(head.get().getNext()).getValue());
    }

    public Optional<T> peek() {
        if (head.get() == fenceNode)
            return empty();

        return of(head.get().getValue());
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
