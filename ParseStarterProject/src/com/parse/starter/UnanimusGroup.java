package com.parse.starter;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Model for a group of users.
 */
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    public ParseUser getMember() { return getParseUser("user"); }

    public void setMember(ParseUser usr) { put("user", usr); }

    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }
}
