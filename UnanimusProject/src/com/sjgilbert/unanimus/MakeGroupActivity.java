package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;

/**
 * A button that creates a group_activity.
 */
@Deprecated
public class MakeGroupActivity extends UnanimusActivityTitle {

    public MakeGroupActivity() {
        super("ppa");
    }

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.make_group_activity);
        try {
            setTitleBar(R.string.ma_title, (ViewGroup) findViewById(R.id.make_group_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        View echoGroup = findViewById(R.id.mga_echo_group_id);
        if (null == echoGroup) {
            new NullPointerException().printStackTrace();
            return;
        }
        TextView groupIDTextView;
        if (echoGroup instanceof TextView) {
            groupIDTextView = (TextView) echoGroup;
        } else {
            new ClassCastException().printStackTrace();
            return;
        }
        groupIDTextView.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void makeGroup(View v) {
        final UnanimusGroup newGroup = new UnanimusGroup();
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        newGroup.setACL(acl);
        newGroup.put("user", ParseUser.getCurrentUser());
        ArrayList<String> members = new ArrayList<>();
        members.add(ParseUser.getCurrentUser().getString("facebookID"));
        newGroup.put("members", members);

        ArrayList<String> restaurants = new ArrayList<>(VotingActivity.NUMBER_OF_RESTAURANTS);
        for (int i = 0; i < VotingActivity.NUMBER_OF_RESTAURANTS; i++) {
            restaurants.add(String.format("Restaurant %d", i + 1));
        }
        newGroup.put("restaurants", restaurants);

        newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(MakeGroupActivity.this, "Success!", Toast.LENGTH_LONG).show();

                    displayGroupID(newGroup);
                } else {
                    Toast.makeText(MakeGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayGroupID(ParseObject group) {
        String groupID = group.getObjectId();
        String text = "Group ID: " + groupID;
        TextView idTextView = (TextView) findViewById(R.id.mga_echo_group_id);
        idTextView.setText(text);
        idTextView.setVisibility(View.VISIBLE);
    }
}
