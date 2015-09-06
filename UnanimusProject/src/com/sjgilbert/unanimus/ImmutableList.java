package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 9/4/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */

class ImmutableList<E> extends ArrayList<E> {
    private final ArrayList<E> arrayList;

    ImmutableList(int size) {
        this.arrayList = new ArrayList<>(size);
        for (int i = 0; size > i; ++i) this.arrayList.add(null);
    }

    ImmutableList(Collection<E> collection) {
        this.arrayList = new ArrayList<>(collection.size());

        for (E e : collection) this.arrayList.add(e);
    }

    @Override
    public void add(int location, E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        return arrayList.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return arrayList.containsAll(collection);
    }

    @Override
    public E get(int location) {
        return arrayList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return arrayList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return arrayList.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<E> iterator = arrayList.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int lastIndexOf(Object object) {
        return arrayList.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int location) {
        final int finLocation = location;

        return new ListIterator<E>() {
            final ListIterator<E> stringListIterator = arrayList.listIterator(finLocation);

            @Override
            public void add(E object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return stringListIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return stringListIterator.hasPrevious();
            }

            @Override
            public E next() {
                return stringListIterator.next();
            }

            @Override
            public int nextIndex() {
                return stringListIterator.nextIndex();
            }

            @Override
            public E previous() {
                return stringListIterator.previous();
            }

            @Override
            public int previousIndex() {
                return stringListIterator.previousIndex();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E object) {
                stringListIterator.set(object);
            }
        };
    }

    @Override
    public E remove(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int location, E object) {
        return arrayList.set(location, object);
    }

    @Override
    public int size() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public List<E> subList(int start, int end) {
        return arrayList.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return arrayList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] array) {
        //noinspection SuspiciousToArrayCall
        return arrayList.toArray(array);
    }
}

