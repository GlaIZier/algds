package ru.glaizier.algds.ds.concurrent;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Value;

public class CopyOnWriteBinaryTree<T extends Comparable> {

    private volatile Node<T> root;

    private final AtomicLong version = new AtomicLong();

    @Value
    private static class Node<T extends Comparable> {
        Node<T> left;
        Node<T> right;
        T value;
    }

    public void add(T value) {

    }

}
