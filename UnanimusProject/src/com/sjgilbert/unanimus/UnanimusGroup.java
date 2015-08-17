package com.sjgilbert.unanimus;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Model for a group_activity of users.
 */
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    private ArrayList<String> members;
    private LatLng location;
    private GroupSettingsPickerActivity.GspaContainer gspaContainer;
    private ArrayList<String> restaurants;
    private ArrayList<ArrayList<Integer>> voteArrays;
    private String recommendation;
    private boolean allVotesIn;

    public UnanimusGroup() {
        members = new ArrayList<>();
        restaurants = new ArrayList<>();
        allVotesIn = false;
    }

    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }

    public ArrayList<String> getMembers() {
        Object o_members = get("members");

        if (null == o_members)
            throw new NullPointerException();

        if (!(o_members instanceof ArrayList))
            throw new ClassCastException();

        ArrayList al_members = (ArrayList) o_members;

        ArrayList<String> al_p_members = new ArrayList<>();
        for (Object o : al_members)
            if (o instanceof String)
                    al_p_members.add((String) o);

        return al_p_members;
    }

    public ArrayList<String> getRestaurants() {
        Object o_restaurants = get("restaurants");

        if (null == o_restaurants)
            throw new NullPointerException();

        if (!(o_restaurants instanceof ArrayList))
            throw new ClassCastException();

        ArrayList al_restaurants = (ArrayList) o_restaurants;

        ArrayList<String> al_p_restaurants = new ArrayList<>();
        for (Object o : al_restaurants)
            if (o instanceof String)
                al_p_restaurants.add((String) o);
        allVotesIn = false;

        return al_p_restaurants;
    }

    public void addVoteArray(ArrayList<Integer> voteArray) {
        add("voteArrays", voteArray);
        saveInBackground();
    }

    public ArrayList<Integer> voteTally() {
        ArrayList<Integer> voteSum = new ArrayList<>();
        for(int x = 0; x < VotingActivity.NUMBER_OF_RESTAURANTS; x++) {
            voteSum.add(0);
        }
        JSONArray array = getJSONArray("voteArrays");
        for (int i = 0; i < array.length(); i++) {
            ArrayList<Integer> oneUsersVotes = null;
            try {
                JSONArray vA = array.getJSONArray(i);
                ArrayList<Integer> vAL = new ArrayList<>();
                for (int k = 0; k < VotingActivity.NUMBER_OF_RESTAURANTS; k++) {
                    vAL.add(vA.getInt(k));
                }
                oneUsersVotes = vAL;
                for (int j = 0; j < getRestaurants().size(); j++) {
                    voteSum.set(j, (voteSum.get(j) + oneUsersVotes.get(j)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return voteSum;
    }

    public String getBestRestaurant(ArrayList<Integer> talliedVotes) {
        int winIndex = talliedVotes.get(0);
        for (int i = 0; i < talliedVotes.size(); i++) {
                int num = talliedVotes.get(i);
            if (num > winIndex) {
                winIndex = i;
            }
        }
        return getRestaurants().get(winIndex);
    }

    public boolean checkIfComplete() {
        if (!allVotesIn && (getJSONArray("voteArrays").length() == getMembers().size())) {
            allVotesIn = !allVotesIn;
            recommendation = getBestRestaurant(voteTally());
            put("recommendation", recommendation);
            saveInBackground();
            return true;
        } else {
            System.out.println("not all votes in");
            return false;
        }
    }
}
