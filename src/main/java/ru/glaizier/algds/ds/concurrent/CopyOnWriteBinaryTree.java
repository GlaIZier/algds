package ru.glaizier.algds.ds.concurrent;

public class CopyOnWriteBinaryTree<T> {

    private static class Node<T> {
        private final Node<T> left;
        private final Node<T> right;
        public Node(Node<T> left, Node<T> right) {
            this.left = left;
            this.right = right;
        }
    }
}
