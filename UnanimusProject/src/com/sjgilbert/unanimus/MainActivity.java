package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * This class shows the groups a user is a part of, as well as allows the
 * user to access the make and join group_activity activities.
 */
public class MainActivity extends UnanimusActivityTitle {
    //    private final GroupQueryWorker groupQueryWorker = new GroupQueryWorker();
    private ParseQueryAdapter<CgaContainer> groupQueryAdapter;

    public MainActivity() {
        super("ma");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        try {
            setTitleBar(R.string.ma_title, (ViewGroup) findViewById(R.id.main_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        //Facebook Picture
        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.ma_prof_pic);
        profilePictureView.setProfileId(ParseUser.getCurrentUser().getString("facebookID"));

        //Shows all the groups user is a member of
        ParseQueryAdapter.QueryFactory<CgaContainer> factory =
                new ParseQueryAdapter.QueryFactory<CgaContainer>() {
                    @Override
                    public ParseQuery<CgaContainer> create() {
                        ParseQuery<CgaContainer> query = ParseQuery.getQuery(CgaContainer.class);
                        query.include(ParseCache.OBJECT_ID);
//                        query.whereEqualTo("members", Profile.getCurrentProfile().getId());
                        query.orderByDescending("createdAt");
                        return query;
                    }
                };

        groupQueryAdapter = new ParseQueryAdapter<CgaContainer>(this, factory) {
            @Override
            public View getItemView(CgaContainer group, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.unanimus_group_abstract, null);
                }
                final TextView groupView = (TextView) view.findViewById(R.id.uga_groupID_view);
//                final ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.uga_invited_by);
                try {
                    ArrayList<String> members = group.getMembers();
                    final ArrayList<String> usernames = new ArrayList<>();
                    final GraphRequest[] requests = new GraphRequest[members.size()];
                    for (int i = 0; i < members.size(); i++) {
                        final String user = members.get(i);
                        requests[i] = new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                String.format("/%s", user),
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        if (response.getError() != null) {
                                            log(
                                                    ELog.e,
                                                    response.getError().getErrorMessage(),
                                                    response.getError().getException()
                                            );
                                            return;
                                        }
                                        try {
                                            usernames.add(response.getJSONObject().getString("name"));
                                            ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.uga_invited_by);
                                            profilePictureView.setProfileId(user);
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
                            groupView.setText(usernames.get(0));
                        }
                    });

                    requestBatch.executeAsync();

//                    groupView.setText(group.getMembers().toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return view;
            }
        };

        groupQueryAdapter.setAutoload(false);

        ListView groupListView = (ListView) findViewById(R.id.ma_groups_list_view);
        groupListView.setAdapter(groupQueryAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CgaContainer selectedGroup = groupQueryAdapter.getItem(position);
                String groupID = selectedGroup.getObjectId();
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                intent.putExtra(GroupActivity.GROUP_ID, groupID);
                startActivity(intent);
            }
        });
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void ma_viewCreateGroup(View view) {
        startActivity(
                new Intent(
                        this,
                        CreateGroupActivity.class
                )
        );
    }

    private void doListQuery() {
        groupQueryAdapter.loadObjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doListQuery();
    }

}
