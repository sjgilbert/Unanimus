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

    final int vote;

    public Vote() throws ParseException {
        super();

        if (!has(VOTE))
            throw new ParseException(ParseException.OTHER_CAUSE, "Used parameter-less constructor, but missing keys");

        vote = getInt(VOTE);
    }

    private Vote(EVote vote) {
        this.vote = vote.num;

        put(VOTE, this.vote);
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

