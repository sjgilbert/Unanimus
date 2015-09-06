package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sam on 9/1/15.
 */
@SuppressWarnings("WeakerAccess")
@ParseClassName("VotesList")
public class VotesList extends ParseObject implements List<Vote> {
    private static final String VOTES_LIST = "votesList";

    private final ImmutableList<Vote> votes;

    public VotesList() {
        final List<Vote> list = getList(VOTES_LIST);
        final int size = list.size();

        votes = new ImmutableList<>(size);

        for (int i = 0; size > i; ++i) votes.set(i, list.get(i));
    }

    static ParseQuery<VotesList> getQuery() {
        return ParseQuery.getQuery(VotesList.class);
    }


    VotesList(
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

        parseACL.setWriteAccess(admin, true);
        parseACL.setReadAccess(admin, true);

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
        put(VOTES_LIST, votes);
    }

    @Override
    public void add(int location, Vote object) {
        votes.add(location, object);
        commit();
    }

    @Override
    public boolean add(Vote object) {
        boolean ret = votes.add(object);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends Vote> collection) {
        boolean ret = votes.addAll(location, collection);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Vote> collection) {
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
    public Vote get(int location) {
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
    public Iterator<Vote> iterator() {
        return votes.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return votes.lastIndexOf(object);
    }

    @Override
    public ListIterator<Vote> listIterator() {
        return votes.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Vote> listIterator(int location) {
        return votes.listIterator();
    }

    @Override
    public Vote remove(int location) {
        Vote ret = votes.remove(location);
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
    public Vote set(int location, Vote object) {
        Vote ret = votes.set(location, object);
        commit();
        return ret;
    }

    @Override
    public int size() {
        return votes.size();
    }

    @NonNull
    @Override
    public List<Vote> subList(int start, int end) {
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
