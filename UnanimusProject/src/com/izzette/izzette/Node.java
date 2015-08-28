package com.izzette.queuemap;

import java.util.AbstractMap;
import java.util.Map;

/**
 * 8/30/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
final class Node<K, V> extends AbstractMap.SimpleEntry<K, V> implements Cloneable, Map.Entry<K, V> {
    private Node<K, V> addedFirst;
    private Node<K, V> addedLast;

    Node(K key, V value) {
        super(key, value);
    }

    Node(Map.Entry<K, V> entry) {
        super(entry);
    }

    Node<K, V> getAddedFirst() {
        return addedFirst;
    }

    Node<K, V> getAddedLast() {
        return addedLast;
    }

    void remove() {
        addedFirst.addedLast = addedLast;
        addedLast.addedFirst = addedFirst;

        unlink();
    }

    void addLast(Node<K, V> node)  {
        node.addedLast = addedLast;
        addedFirst.addedFirst = node;

        node.addedFirst = this;
        addedLast = node;
    }

    public void addFirst(Node<K, V> object) {
        addedFirst.addLast(object);
    }

    private void unlink() {
        checkNotInList();

        addedFirst = null;
        addedLast = null;
    }

    private void checkNotInList() {
        if (addedFirst.addedLast == this || addedLast.addedFirst == this)
            throw new IllegalStateException();
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public Object clone() {
        Node<K, V> c;
        try {
            //noinspection unchecked
            c = (Node<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            c = new Node<>(getKey(), getValue());
        }

        c.unlink();

        return c;
    }
}

