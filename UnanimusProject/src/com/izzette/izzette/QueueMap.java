package com.izzette.queuemap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * 8/30/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class QueueMap<K, V> {
    public final Queue<Entry<K, V>> asQueue;
    public final Map<K, V> asMap;

    private final Map<K, Node<K, V>> map = new HashMap<>();
    private final Queue<Node<K, V>> queue = new LinkedNodeQueue<>();

    public QueueMap() {
        asQueue = new AsQueue(queue);
        asMap = new AsMap(map);
    }

    private void clear() {
        map.clear();
        queue.clear();
    }

    private Node<K, V> putAdd(Node<K, V> node) {
        queue.add(node);
        return map.put(node.getKey(), node);
    }

    private Node<K, V> putAdd(Entry<K, V> entry) {
        Node<K, V> node = new Node<>(entry);
        queue.add(node);
        return map.put(node.getKey(), node);
    }

    private Node<K, V> putAdd(K key, V value) {
        return putAdd(new Node<>(key, value));
    }

    private boolean isEmpty() {
        return queue.isEmpty();
    }

    private int size() {
        return queue.size();
    }

    private Node<K, V> remove(Object key) {
        Node<K, V> node = map.remove(key);
        if (node != null) node.remove();
        return node;
    }

    private boolean containsValue(Object value) {
        for (Node<K, V> node : queue)
            if (node.getValue() == value)
                return true;

        return false;
    }

    private Iterator<Entry<K, V>> iterator() {
        return new Itr(queue.iterator());
    }

    private Collection<V> values() {
        Collection<V> collection = new LinkedList<>();
        for (Entry<K, V> entry : queue) collection.add(entry.getValue());
        return collection;
    }

    private class AsMap
            implements Map<K, V> {
        private final Map<K, Node<K, V>> map;

        private AsMap(Map<K, Node<K, V>> map) {
            this.map = map;
        }

        @Override
        public void clear() {
            QueueMap.this.clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return QueueMap.this.containsValue(value);
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new EntrySet();
        }

        @Override
        public V get(Object key) {
            return map.get(key).getValue();
        }

        @Override
        public boolean isEmpty() {
            return QueueMap.this.isEmpty();
        }

        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        public V put(K key, V value) {
            return QueueMap.this.putAdd(key, value).getValue();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> otherMap) {
            for (Entry<? extends K, ? extends V> entry : otherMap.entrySet())
                put(entry.getKey(), entry.getValue());
        }

        @Override
        public V remove(Object key) {
            return QueueMap.this.remove(key).getValue();
        }

        @Override
        public int size() {
            return QueueMap.this.size();
        }

        @Override
        public Collection<V> values() {
            return QueueMap.this.values();
        }

        private Iterator<Entry<K, V>> iterator() {
            return QueueMap.this.iterator();
        }

        private class EntrySet extends AbstractSet<Entry<K, V>> {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return AsMap.this.iterator();
            }

            @Override
            public int size() {
                return AsMap.this.size();
            }
        }
    }

    private class AsQueue
            implements Queue<Entry<K, V>> {
        private final Queue<Node<K, V>> queue;

        private AsQueue(Queue<Node<K, V>> queue) {
            this.queue = queue;
        }

        @Override
        public boolean add(Entry<K, V> entry) {
            QueueMap.this.putAdd(entry);
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> collection) {
            boolean ret = false;
            for (Entry<K, V> entry : collection) if (add(entry)) ret = true;
            return ret;
        }

        @Override
        public void clear() {
            QueueMap.this.clear();
        }

        @Override
        public boolean contains(Object object) {
            return queue.contains(object);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            boolean ret = false;
            for (Object o : collection) if (contains(o)) ret = true;
            return ret;
        }

        @Override
        public boolean isEmpty() {
            return QueueMap.this.isEmpty();
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return QueueMap.this.iterator();
        }

        @Override
        public boolean remove(Object object) {
            return (null != QueueMap.this.remove(((Entry) object).getKey()));
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
            for (Entry<K, V> entry : queue) if (! collection.contains(entry)) {
                QueueMap.this.remove(entry.getKey());
                ret = true;
            }

            return ret;
        }

        @Override
        public int size() {
            return QueueMap.this.size();
        }

        @Override
        public Object[] toArray() {
            return queue.toArray();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            return queue.toArray(array);
        }

        @Override
        public boolean offer(Entry<K, V> entry) {
            return add(entry);
        }

        @Override
        public Entry<K, V> remove() {
            return QueueMap.this.remove(queue.remove().getKey());
        }

        @Override
        public Entry<K, V> poll() {
            try {
                return remove();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        @Override
        public Entry<K, V> element() {
            return queue.element();
        }

        @Override
        public Entry<K, V> peek() {
            try {
                return queue.element();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    private class Itr implements Iterator<Entry<K, V>> {
        private final Iterator<Node<K, V>> entryIterator;
        private Entry<K, V> lastReturned;

        private Itr(Iterator<Node<K, V>> entryIterator) {
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            lastReturned = entryIterator.next();
            return lastReturned;
        }

        @Override
        public void remove() {
            map.remove(lastReturned.getKey());
            entryIterator.remove();
        }
    }
}
