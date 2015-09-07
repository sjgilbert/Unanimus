package com.sjgilbert.unanimus;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@ParseClassName("Vote")
public class Vote extends ParseObject {
    private static final String VOTE = "vote";

    int vote;

    public Vote() {
        super();
    }

    private Vote(EVote vote) {
        this.vote = vote.num;

        put(VOTE, this.vote);
    }

    void load() throws ParseException {
        fetchIfNeeded();

        if (!has(VOTE))
            throw new IllegalStateException();

        vote = getInt(VOTE);
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

