package com.parse.starter;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Model for a group of users.
 */
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    public ArrayList<ParseUser> getMembers() {
        if(get("members") != null) {
            return (ArrayList<ParseUser>) get("members");
        }
        else return null;
    }

    public ParseUser getMember(int ind) {
        ArrayList<ParseUser> members = (ArrayList<ParseUser>) get("members");
        return members.get(ind);
    }

    public void setMember(ParseUser usr) { add("members", usr); }

    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }
}
