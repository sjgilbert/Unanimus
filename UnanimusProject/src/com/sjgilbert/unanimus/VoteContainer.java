package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("VoteContainer")
public class VoteContainer extends ParseObject implements List<Integer>{
    private ArrayList<Integer> al = new ArrayList<>();

    public VoteContainer() {

    }

    public static ParseQuery<UnanimusGroup2> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup2.class);
    }

    public List<Integer> getVotes() {
        return getList("votes");
    }

    public void setVotes(Collection<String> votes) {
        put("votes", votes);
    }

    @Override
    public void add(int location, Integer object) {
        al.add(location, object);
    }

    @Override
    public boolean add(Integer object) {
        return al.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends Integer> collection) {
        return al.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        return al.addAll(collection);
    }

    @Override
    public void clear() {
        al.clear();
    }

    @Override
    public boolean contains(Object object) {
        return al.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return al.containsAll(collection);
    }

    @Override
    public Integer get(int location) {
        return al.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return al.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return al.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<Integer> iterator() {
        return al.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return al.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<Integer> listIterator() {
        return al.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Integer> listIterator(int location) {
        return al.listIterator(location);
    }

    @Override
    public Integer remove(int location) {
        return al.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return al.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return al.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return al.retainAll(collection);
    }

    @Override
    public Integer set(int location, Integer object) {
        return al.set(location, object);
    }

    @Override
    public int size() {
        return al.size();
    }

    @NonNull
    @Override
    public List<Integer> subList(int start, int end) {
        return al.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }
}
