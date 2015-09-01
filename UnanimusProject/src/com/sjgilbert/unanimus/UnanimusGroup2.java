package com.sjgilbert.unanimus;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.List;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("UnanimusGroup2")
public class UnanimusGroup2 extends ParseObject {

    public static ParseQuery<UnanimusGroup2> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup2.class);
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

    public void setVoteContainerIds(Collection<String> voteContainerIds) {
        addAll("voteContainerIds", voteContainerIds);
    }

    public void setUserIds(Collection<String> userIds) {
        addAll("userIds", userIds);
    }

    public void setRestaurantIds(Collection<String> restaurantIds) {
        addAll("restaurantIds", restaurantIds);
    }

}
