package com.sjgilbert.unanimus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Profile;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.sjgilbert.unanimus.GroupSettingsPickerActivity.*;

/**
 * Activity for creating group.  Calls 3 other activities for input to build group.
 */
public class CreateGroupActivity extends UnanimusActivityTitle {
    private CgaGroup cgaGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_group_activity);
        try {
            setTitleBar(R.string.cga_title, (ViewGroup) findViewById(R.id.create_group_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        cgaGroup = new CgaGroup();
    }

    public void createGroup(View v) throws ParseException {
        final ProgressDialog wait = new ProgressDialog(CreateGroupActivity.this);
        wait.setMessage(getString(R.string.wait_message));
        wait.show();

        final CgaGroup newGroup = new CgaGroup();
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        newGroup.setACL(acl);
        newGroup.put("user", ParseUser.getCurrentUser());
        ArrayList<String> mems = new ArrayList<>();
        mems.add(Profile.getCurrentProfile().getId());
        setGroupMembers(mems);
        newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                wait.dismiss();
                if (e == null) {
                    Toast.makeText(CreateGroupActivity.this, "Success!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setGroupMembers(ArrayList<String> mems) {
        cgaGroup.members = mems;
    }

    private void setLocation(String loc) {
        cgaGroup.location = loc;
    }

    private void setTime(
            int hour,
            int min
    ) {
        cgaGroup.hourOfDay = hour;
        cgaGroup.minute = min;
    }

    private void setDate(
            int year,
            int mon,
            int day
    ) {
        cgaGroup.year = year;
        cgaGroup.month = mon;
        cgaGroup.day = day;
    }

    private void setRestaurants(ArrayList<String> rests) {
        cgaGroup.restaurants = rests;
    }

    private void setRadius(int rad) {
        cgaGroup.radius = rad;
    }

    private void setPriceLevel(int len) {
        try {
            cgaGroup.priceLevel = GspaContainer.EPriceLevel.getPriceLevelFromInt(len);
        }
        catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
    }

    @ParseClassName("CgaGroup")
    protected static class CgaGroup extends ParseObject {
        private ArrayList<String> members;
        private String location;
        private int year, month, day, hourOfDay, minute;
        private int radius;
        private GspaContainer.EPriceLevel priceLevel;
        private ArrayList<String> restaurants;
        private ArrayList<ArrayList<Integer>> voteArrays;
        private String recommendation;
        private boolean allVotesIn;

        public CgaGroup() {
            allVotesIn = false;
        }

        public static ParseQuery<CgaGroup> getQuery() {
            return ParseQuery.getQuery(CgaGroup.class);
        }

        public ArrayList<String> getMembers() {return members;}

        public int[] voteTally() {
            int[] voteSum = new int[restaurants.size()];
            for (ArrayList<Integer> oneUsersVotes : voteArrays) {
                for (int i = 0; i < restaurants.size(); i++) {
                    voteSum[i] = voteSum[i] += oneUsersVotes.get(i);
                }
            }
            return voteSum;
        }

        public String getBestRestaurant(int[] talliedVotes) {
            int winIndex = talliedVotes[0];
            for (int num : talliedVotes) {
                if (num > winIndex) {
                    winIndex = num;
                }
            }
            return restaurants.get(winIndex);
        }

        public boolean checkIfComplete() {
            if (!allVotesIn && voteArrays.size() == members.size()) {
                allVotesIn = !allVotesIn;
                recommendation = getBestRestaurant(voteTally());
                return true;
            }
            else {return false;}
        }

    }
}
