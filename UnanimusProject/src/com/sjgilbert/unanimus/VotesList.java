package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
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
public class VotesList extends ParseObject implements List<Integer> {
    private static final String VOTES_LIST = "votesList";

    private List<Integer> votes;

    static ParseQuery<VotesList> getQuery() {
        return ParseQuery.getQuery(VotesList.class);
    }

    public VotesList() {
        super();
    }

    VotesList(
            int voteLength,
            ParseUser admin,
            String voterId,
            List<String> readers
    ) {
        super();

        votes = new ImmutableList<>(voteLength);

        for (int i = 0; voteLength > i; ++i) {
            votes.set(i, VotesList.getEmptyVote());
        }

        ParseACL parseACL = new ParseACL(admin);

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

    public static Integer getUpVote() {
        return 1;
    }

    public static Integer getDownVote() {
        return -1;
    }

    public static Integer getEmptyVote() {
        return Integer.MIN_VALUE;
    }

    void load() throws ParseException {
        fetchIfNeeded();

        if (!has(VOTES_LIST))
            throw new IllegalStateException();

        votes = getList(VOTES_LIST);
    }

    private void commit() {
        put(VOTES_LIST, votes);
    }

    @Override
    public void add(int location, Integer object) {
        votes.add(location, object);
        commit();
    }

    @Override
    public boolean add(Integer object) {
        boolean ret = votes.add(object);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends Integer> collection) {
        boolean ret = votes.addAll(location, collection);
        commit();
        return ret;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Integer> collection) {
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
    public Integer get(int location) {
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
    public Iterator<Integer> iterator() {
        return votes.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return votes.lastIndexOf(object);
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return votes.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Integer> listIterator(int location) {
        return votes.listIterator();
    }

    @Override
    public Integer remove(int location) {
        Integer ret = votes.remove(location);
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
    public Integer set(int location, Integer object) {
        Integer ret = votes.set(location, object);
        commit();
        return ret;
    }

    @Override
    public int size() {
        return votes.size();
    }

    @NonNull
    @Override
    public List<Integer> subList(int start, int end) {
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
