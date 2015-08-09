package com.sjgilbert.unanimus;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Model for a group_activity of users.
 */
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }

    public ArrayList<ParseUser> getMembers() {
        Object o_members = get("members");

        if (null == o_members)
            throw new NullPointerException();

        if (! (o_members instanceof ArrayList))
            throw new ClassCastException();

        ArrayList al_members = (ArrayList) o_members;

        ArrayList<ParseUser> al_p_members = new ArrayList<>();
        for (Object o : al_members) if (o instanceof ParseUser)
                al_p_members.add((ParseUser) o);

        return al_p_members;
    }

    public ParseUser getMember(int ind) {
        ArrayList<ParseUser> members;
        try {
            members = getMembers();
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            members = new ArrayList<>();
        }
        return members.get(ind);
    }

    public boolean setMember(ParseUser usr) {
        if (!getMembers().contains(usr)) {
            add("members", usr);
            return true;
        }
        return false;
    }

}
