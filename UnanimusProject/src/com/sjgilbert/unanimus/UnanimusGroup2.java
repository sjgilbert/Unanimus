package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("UnanimusGroup2")
public class UnanimusGroup2 extends ParseObject {

    public static ParseQuery<UnanimusGroup2> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup2.class);
    }

    public UnanimusGroup2() {}

    public UnanimusGroup2(
            Collection<String> voteContainerIds,
            Collection<String> userIds,
            Collection<String> restaurantIds) {
        addAll("voteContainerIds", voteContainerIds);
        addAll("userIds", userIds);
        addAll("restaurantIds", restaurantIds);
    }

    public List<String> getVoteContainerIds() {
        return getList("voteContainerIds");
    }

    public List<String> getUserIds() {
        return getList("userIds");
    }

    public List<String> getRestaurantsIds() {
        return getList("restaurantIds");
    }

    public class VoteContainerMap extends ParseObject implements Map<String, VoteContainer> {
        private final Map<String, VoteContainer> vcm = new HashMap<>();

        @Override
        public void clear() {
            vcm.clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return vcm.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return vcm.containsValue(value);
        }

        @NonNull
        @Override
        public Set<Entry<String, VoteContainer>> entrySet() {
            return vcm.entrySet();
        }

        @Override
        public VoteContainer get(Object key) {
            return vcm.get(key);
        }

        @Override
        public boolean isEmpty() {
            return vcm.isEmpty();
        }

        @Override
        public VoteContainer put(String key, VoteContainer value) {
            return vcm.put(key, value);
        }

        @Override
        public void putAll(@NonNull Map<? extends String, ? extends VoteContainer> map) {
            vcm.putAll(map);
        }

        @Override
        public VoteContainer remove(Object key) {
            return vcm.remove(key);
        }

        @Override
        public int size() {
            return vcm.size();
        }

        @NonNull
        @Override
        public Collection<VoteContainer> values() {
            return vcm.values();
        }
    }
}
