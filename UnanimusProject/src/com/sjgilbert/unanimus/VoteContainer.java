package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("VoteContainer")
public class VoteContainer extends ParseObject implements List<String> {
    private static final String VOTES_KEY = "votesKey";

    private final ImmutableList<String> votes;

    // Used by Parse, see annotation @ParseClassName
    public VoteContainer() {
        final List<String> list = getList(VOTES_KEY);
        final int size = list.size();

        votes = new ImmutableList<>(size);

        for (int i = 0; size > i; ++i) votes.set(i, list.get(i));
    }

    VoteContainer(
            int voteLength,
            ParseUser admin,
            String voterId,
            List<String> readers
    ) {
        super();

        votes = new ImmutableList<>(voteLength);

        ParseACL parseACL = new ParseACL(admin);

        parseACL.setPublicReadAccess(false);
        parseACL.setPublicWriteAccess(false);
        parseACL.setWriteAccess(voterId, true);
        parseACL.setReadAccess(voterId, true);

        for (String r : readers) {
            parseACL.setReadAccess(r, true);
            parseACL.setWriteAccess(r, false);
        }

        setACL(parseACL);

        commit();
    }

    private void commit() {
        put(VOTES_KEY, votes);
    }

    @Override
    public void add(int location, String object) {
        votes.add(location, object);
        commit();
    }

    @Override
    public boolean add(String object) {
        boolean ret = votes.add(object);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends String> collection) {
        boolean ret = votes.addAll(location, collection);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends String> collection) {
        boolean ret = votes.addAll(collection);
        commit();
        return ret;
    }

    @Override
    public void clear() {
        votes.clear();
        commit();
    }

    @Override
    public boolean contains(Object object) {
        return votes.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return votes.containsAll(collection);
    }

    @Override
    public String get(int location) {
        return votes.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return votes.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return votes.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return votes.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return votes.lastIndexOf(object);
    }

    @Override
    public ListIterator<String> listIterator() {
        return votes.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<String> listIterator(int location) {
        return votes.listIterator();
    }

    @Override
    public String remove(int location) {
        String ret = votes.remove(location);
        commit();
        return ret;
    }

    @Override
    public boolean remove(Object object) {
        boolean ret = votes.remove(object);
        commit();
        return ret;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean ret = votes.removeAll(collection);
        commit();
        return ret;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean ret = votes.retainAll(collection);
        commit();
        return ret;
    }

    @Override
    public String set(int location, String object) {
        String ret = votes.set(location, object);
        commit();
        return ret;
    }

    @Override
    public int size() {
        return votes.size();
    }

    @NonNull
    @Override
    public List<String> subList(int start, int end) {
        return votes.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return votes.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] array) {
        //noinspection SuspiciousToArrayCall
        return votes.toArray(array);
    }
}
