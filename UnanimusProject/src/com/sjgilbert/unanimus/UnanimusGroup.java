package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Model for a group_activity of users.
 */
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    public final FriendPickerActivity.FpaContainer fpaContainer;
    public final GroupSettingsPickerActivity.GspaContainer gspaContainer;
    public final PlacePickActivity.PpaContainer ppaContainer;
    private EStatus status;
    public UnanimusGroup() {
        gspaContainer = new GroupSettingsPickerActivity.GspaContainer();
        fpaContainer = new FriendPickerActivity.FpaContainer();
        ppaContainer = new PlacePickActivity.PpaContainer();
        status = EStatus.pending;
    }

    public static ParseQuery<UnanimusGroup> getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }

    public EStatus getStatus() {
        return status;
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

    @Deprecated
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

        return al_p_restaurants;
    }

    @Deprecated
    public void addVoteArray(ArrayList<Integer> voteArray) {
        add("voteArrays", voteArray);
        saveInBackground();
    }

    @Deprecated
    private ArrayList<Integer> voteTally() {
        ArrayList<Integer> voteSum = new ArrayList<>();
        for (int x = 0; x < VotingActivity.NUMBER_OF_RESTAURANTS; x++) {
            voteSum.add(0);
        }
        JSONArray array = getJSONArray("voteArrays");
        for (int i = 0; i < array.length(); i++) {
            ArrayList<Integer> oneUsersVotes;
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

    @Deprecated
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

    @Deprecated
    public boolean checkIfComplete() {
        if ((getJSONArray("voteArrays").length() == getMembers().size())) {
            String recommendation = getBestRestaurant(voteTally());
            put("recommendation", recommendation);
            saveInBackground();
            return true;
        } else {
            Log.i(
                    "Unanimus",
                    "Voting not complete"
            );
            return false;
        }
    }

    @SuppressWarnings("unused")
    GroupSettingsPickerActivity.GspaContainer getGspaContainer() {
        return gspaContainer;
    }

    public void setGspaContainer(Bundle bundle) {
        gspaContainer.setFromBundle(bundle);
    }

    @SuppressWarnings("unused")
    FriendPickerActivity.FpaContainer getFpaContainer() {
        return fpaContainer;
    }

    public void setFpaContainer(Bundle bundle) {
        fpaContainer.setFromBundle(bundle);
    }

    @SuppressWarnings("unused")
    PlacePickActivity.PpaContainer getPpaContainer() {
        return ppaContainer;
    }

    public void setPpaContainer(Bundle bundle) {
        ppaContainer.setFromBundle(bundle);
    }

    private enum EStatus {
        pending,
        complete,
        inProgress,
        unread
    }
}
