package com.izzette.queuemap;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * 8/30/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
class LinkedNodeQueue<K, V>
    implements Queue<Node<K, V>> {
    private final Node<K, V> sentinel = new Node<>(null, null);

    @Override
    public boolean add(Node<K, V> object) {
        sentinel.addFirst(object);
        return true;
    }

    @Override
    public boolean offer(Node<K, V> kvNode) {
        return add(kvNode);
    }

    @Override
    public Node<K, V> remove() {
        Node<K, V> addedLast = element();
        addedLast.remove();
        return addedLast;
    }

    @Override
    public Node<K, V> poll() {
        try {
            return remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public Node<K, V> element() {
        Node<K, V> addedLast = sentinel.getAddedLast();
        if (addedLast == sentinel) throw new NoSuchElementException();
        return addedLast;
    }

    @Override
    public Node<K, V> peek() {
        try {
            return element();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Node<K, V>> collection) {
        boolean ret = false;
        for (Node<K, V> node : collection) if (add(node)) ret = true;
        return ret;
    }

    @Override
    public void clear() {
        sentinel.addLast(sentinel);
        sentinel.addFirst(sentinel);
    }

    @Override
    public boolean contains(Object object) {
        for (Node<K, V> node : this) if (node == object) return true;
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        boolean ret = false;
        for (Object o : collection) if (contains(o)) ret = true;
        return ret;
    }

    @Override
    public boolean isEmpty() {
        return (sentinel.getAddedLast() == sentinel);
    }

    @Override
    public Iterator<Node<K, V>> iterator() {
        return new LastToFirstNodeItr();
    }

    @Override
    public boolean remove(Object object) {
        for (Node<K, V> node : this) if (node == object) {
            node.remove();
            return true;
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean ret = false;
        for (Object o : collection) if (remove(o)) ret = true;
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean ret = false;
        for (Node<K, V> node : this) if (! collection.contains(node)) {
            remove(node.getKey());
            ret = true;
        }

        return ret;
    }

    @Override
    public int size() {
        int i = 0;
        for (Node<K, V> ignore : this) ++i;
        return i;
    }

    @Override
    public Object[] toArray() {
        Node[] array = new Node[size()];
        return toArray(array);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        int i = 0;
        try {
            for (Node<K, V> node : this) {
                array[i] = (T) node;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return (T[]) toArray();
        }
        return array;
    }

    private final class LastToFirstNodeItr
            implements Iterator<Node<K, V>> {
        private Node<K, V> next;
        private Node<K, V> lastReturned;

        protected LastToFirstNodeItr() {
            this.next = LinkedNodeQueue.this.sentinel;
            advance();
        }

        private void advance() {
            next = next.getAddedFirst();
        }

        @Override
        public final boolean hasNext() {
            return (next != LinkedNodeQueue.this.sentinel);
        }

        @Override
        public final Node<K, V> next() {
            if (! hasNext()) throw new NoSuchElementException();

            lastReturned = next;

            advance();

            return lastReturned;
        }

        @Override
        public final void remove() {
            if (null == lastReturned) throw new IllegalStateException();
            lastReturned.remove();
            lastReturned = null;
        }
    }
}
