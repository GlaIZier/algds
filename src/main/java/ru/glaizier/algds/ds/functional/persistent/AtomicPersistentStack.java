package ru.glaizier.algds.ds.functional.persistent;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AtomicPersistentStack<T> {

    private static final Node FENCE_NODE = new Node<>(null, null);

    @SuppressWarnings("unchecked")
    private AtomicReference<Node<T>> head = new AtomicReference<>(FENCE_NODE);

    @Data
    @AllArgsConstructor
    private static class Node<T> {
        private final T value;
        private Node<T> next;
    }

    public void push(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        head.getAndUpdate(prevHead -> new Node<>(value, prevHead));
    }


    /**
     * @param value
     * @param index if index is greater than the number of elements, than it will be add the last
     */
    public void add(int index, T value) {
        if (index < 0 || value == null) {
            throw new IllegalArgumentException();
        } else if (index == 0 || head.get() == FENCE_NODE) {
            push(value);
            return;
        }

        head.getAndUpdate(prevHead -> {
            // create a brand new list before index
            Node<T> curPrev = prevHead.getNext();
            // create a new head
            Node<T> newHead = new Node<>(prevHead.getValue(), prevHead.getNext());
            Node<T> curNew = newHead;

            // copying loop
            int i = 1;
            for (; i < index && curPrev != FENCE_NODE; curNew = curNew.getNext(), curPrev = curPrev.getNext(), i++) {
                Node<T> copyNode = new Node<>(curPrev.getValue(), curPrev.getNext());
                curNew.setNext(copyNode);
            }

            // add a new node
            Node<T> putNode = new Node<>(value, curNew.getNext());
            curNew.setNext(putNode);
            return newHead;
        });
    }

    public Optional<T> pop() {
        if (head.get() == FENCE_NODE)
            return empty();

        return of(head.getAndUpdate(Node::getNext).getValue());
    }

    public Optional<T> peek() {
        if (head.get() == FENCE_NODE)
            return empty();

        return of(head.get().getValue());
    }

    public boolean contains(T value) {
        Node<T> cur = head.get();
        while (cur != FENCE_NODE && !value.equals(cur.getValue())) {
            cur = cur.getNext();
        }
        return cur != FENCE_NODE;
    }

    public Optional<T> get(int index) {
        if (index < 0)
            throw new IllegalArgumentException();

        Node<T> cur = head.get();
        int i = 0;
        while (cur != FENCE_NODE && i < index) {
            cur = cur.getNext();
            i++;
        }
        return (cur == FENCE_NODE) ?
                empty() :
                of(cur.value);
    }

    public void forEach(Consumer<? super T> action) {
        for(Node<T> cur = head.get(); cur != FENCE_NODE; cur = cur.getNext())
            action.accept(cur.getValue());
    }

}
