package com.sjgilbert.unanimus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;

import static com.sjgilbert.unanimus.GroupSettingsPickerActivity.*;

/**
 * Activity for creating group.  Calls 3 other activities for input to build group.
 */
public class CreateGroupActivity extends UnanimusActivityTitle {
    private final int FPA_REQUEST = 1;
    private final int GSPA_REQUEST = 2;
    private final int PPA_REQUEST = 3;

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

    public void createGroup(View view) throws ParseException {
        final ProgressDialog wait = new ProgressDialog(CreateGroupActivity.this);
        wait.setMessage(getString(R.string.wait_message));
        wait.show();

        final CgaGroup newGroup = new CgaGroup();

        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);

        newGroup.setACL(acl);
        newGroup.put("user", ParseUser.getCurrentUser());
        ArrayList<String> members = new ArrayList<>();
        members.add(Profile.getCurrentProfile().getId());
        setGroupMembers(members);

        launchGspaForResult();

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

    private void setGroupMembers(ArrayList<String> groupMembers) {
        cgaGroup.members = groupMembers;
    }

    private void setLocation(LatLng location) {
        cgaGroup.location = location;
    }

    private void setRestaurants(ArrayList<String> restaurants) {
        cgaGroup.restaurants = restaurants;
    }

    private void setGspaContainer(GspaContainer gspaContainer) {
        cgaGroup.gspaContainer = gspaContainer;
    }

    private GspaContainer getGspaContainerFromBundle(Bundle bundle) {
        return new GspaContainer(bundle);
    }

    private void launchGspaForResult() {
        startActivityForResult(
                new Intent(this, GroupSettingsPickerActivity.class),
                GSPA_REQUEST
        );
    }

    private void processGspaResult(Intent result) {
        Bundle gspaBundle = result.getBundleExtra(GroupSettingsPickerActivity.GSPA);
        GspaContainer gspaContainer = getGspaContainerFromBundle(gspaBundle);
        setGspaContainer(gspaContainer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            Log.d(
                    getString(R.string.app_name),
                    String.format(
                            "%s. Request code: %d Result code: %d",
                            "CreateGroupActivity got non-OK result from activity",
                            requestCode,
                            resultCode
                    )
            );
            return;
        }

        switch (requestCode) {
            case GSPA_REQUEST:
                processGspaResult(data);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @ParseClassName("CgaGroup")
    static class CgaGroup extends ParseObject {
        private ArrayList<String> members;
        private LatLng location;
        private GspaContainer gspaContainer;
        private ArrayList<String> restaurants;
        private ArrayList<ArrayList<Integer>> voteArrays;

        public CgaGroup() {
            members = new ArrayList<>();
        }

        public static ParseQuery<CgaGroup> getQuery() {
            return ParseQuery.getQuery(CgaGroup.class);
        }

        public ArrayList<String> getMembers() {return members;}

    }
}
