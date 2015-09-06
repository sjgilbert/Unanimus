package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * The activity which displays a specific group_activity a user is a part of.  Should
 * eventually allow the user to indicate preferences/view recommendations.
 */
public class GroupActivity extends UnanimusActivityTitle {
    public static final String GROUP_ID = "GROUP_ID";
    private static final String GA = "ga";
    private String groupName;
    private CgaContainer group;


    public GroupActivity() {
        super(GA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_activity);
        try {
            setTitleBar(R.string.ga_title, (ViewGroup) findViewById(R.id.group_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();    //The GROUP_ID of the selected group_activity
        if (extras != null) {
            groupName = extras.getString(GROUP_ID);
        } else {
            Toast.makeText(GroupActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        //Setting the group_activity name at top
        TextView groupNameTextView = (TextView) findViewById(R.id.ga_name);
        groupNameTextView.setText("GROUP ID: " + groupName);

        //Query for the group_activity's data
        ParseQuery query = ParseQuery.getQuery(CgaContainer.class);
        query.whereEqualTo("objectId", groupName);
        ParseCache.parseCache.put(groupName, (ParseQuery<ParseObject>) query);
        try {
            group = (CgaContainer) query.getFirst();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        UnanimusGroup.Builder builder = new UnanimusGroup.Builder(group);
        builder.getInBackground(new UnanimusGroup.Builder.Callback() {
            @Override
            public void done(UnanimusGroup unanimusGroup) {
                unanimusGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) log(ELog.e, e.getMessage(), e);
                    }
                });
            }
        });

        //Setting owner of group_activity
        TextView createdBy = (TextView) findViewById(R.id.ga_created_by);
        createdBy.setText("Created by " + Profile.getCurrentProfile().getName());

        //Setting members of group_activity
        ArrayList<String> members = group.getMembers();
        final ArrayList<String> usernames = new ArrayList<>();
        GraphRequest[] requests = new GraphRequest[members.size()];
        for (int i = 0; i < members.size(); i++) {
            String user = members.get(i);
            requests[i] = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    String.format("/%s", user),
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                usernames.add(response.getJSONObject().getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
        GraphRequestBatch requestBatch = new GraphRequestBatch(requests);
        requestBatch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequestBatch) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(GroupActivity.this, R.layout.members_fragment, usernames);
                ListView membersList = (ListView) findViewById(R.id.ga_members_list);
                membersList.setAdapter(adapter);
            }
        });

        requestBatch.executeAsync();
    }

    @SuppressWarnings("unused")
    public void ga_viewStartVotingActivity(@SuppressWarnings("UnusedParameters") View view) {
        Intent intent = new Intent(GroupActivity.this, VotingActivity.class);
        intent.putExtra(GROUP_ID, groupName);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void ga_viewStartRecommendationActivity(@SuppressWarnings("UnusedParameters") View view) {
        if (group.get("recommendation") != null) {
            Intent intent = new Intent(GroupActivity.this, RecommendationActivity.class);
            intent.putExtra(GROUP_ID, groupName);
            startActivity(intent);
        }
    }
}
