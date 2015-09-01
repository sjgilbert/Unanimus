package com.sjgilbert.unanimus;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.List;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("VoteContainer")
public class VoteContainer extends ParseObject {

    public static ParseQuery<UnanimusGroup2> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup2.class);
    }

    public List<Integer> getVotes() {
        return getList("votes");
    }

    public void setVotes(Collection<String> votes) {
        put("votes", votes);
    }
}
