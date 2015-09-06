package com.sjgilbert.unanimus;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@ParseClassName("Vote")
public class Vote extends ParseObject {
    public static final String SINGLE_VOTE_KEY = "singleVote";

    final int vote;

    public Vote() {
        vote = getInt(SINGLE_VOTE_KEY);
    }

    private Vote(EVote vote) {
        this.vote = vote.num;

        put(SINGLE_VOTE_KEY, vote);
    }

    public static Vote getUpVote() {
        return new Vote(EVote.up);
    }

    public static Vote getDownVote() {
        return new Vote(EVote.down);
    }

    public static Vote getSkipVote() {
        return new Vote(EVote.skip);
    }

    private enum EVote {
        up(1),
        down(-1),
        skip(0);

        final int num;

        EVote(int num) {
            this.num = num;
        }
    }
}

