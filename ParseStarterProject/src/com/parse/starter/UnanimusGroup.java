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
    public ArrayList<String> getMembers() {
        if(get("members") != null) {
            return (ArrayList<String>) get("members");
        }
        else return null;
    }

    public String getMember(int ind) {
        ArrayList<String> members = (ArrayList<String>) get("members");
        return members.get(ind);
    }

    public boolean setMember(String usr) {
        if (!getMembers().contains(usr)) {
            add("members", usr);
            return true;
        }
        return false;
    }

    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }

}
