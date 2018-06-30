package ru.glaizier.algds.ds.functional.immutable;

import lombok.Value;

import java.util.concurrent.atomic.AtomicReference;

public class ImmutableLinkedList<T> {

    private AtomicReference<Node<T>> head;

    @Value
    private static class Node<T> {
        T value;
        Node<T> next;
    }

}
