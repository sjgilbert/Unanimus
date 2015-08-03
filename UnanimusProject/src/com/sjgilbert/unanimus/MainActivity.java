package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * This class shows the groups a user is a part of, as well as allows the
 * user to access the make and join group activities.
 */
public class MainActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //Button to join group
        Button joinGroupButton = (Button) findViewById(R.id.main_join_group);
        Profile prof = Profile.getCurrentProfile();
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
                startActivity(intent);
            }
        });

        ProfilePictureView profpic = (ProfilePictureView) findViewById(R.id.prof_pic);
        profpic.setProfileId(prof.getId());

        //Button to make group
        Button makeGroupButton = (Button) findViewById(R.id.main_create_group);
        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        //Shows all the groups user is a member of
        ParseQueryAdapter.QueryFactory<UnanimusGroup> factory =
            new ParseQueryAdapter.QueryFactory<UnanimusGroup>() {
                public ParseQuery<UnanimusGroup> create() {
                    ParseQuery<UnanimusGroup> query = UnanimusGroup.getQuery();
                    query.include("objectID");
                    query.whereEqualTo("members", ParseUser.getCurrentUser());
                    query.orderByDescending("createdAt");
                    return query;
                }
            };

        groupQueryAdapter = new ParseQueryAdapter<UnanimusGroup>(this, factory) {
            @Override
            public View getItemView(UnanimusGroup group, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.unanimus_group_item, null);
                }
                TextView groupView = (TextView) view.findViewById(R.id.groupID_view);
                groupView.setText(group.getObjectId());
                return view;
            }
        };

        groupQueryAdapter.setAutoload(false);

        ListView groupListView = (ListView) findViewById(R.id.groups_listview);
        groupListView.setAdapter(groupQueryAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UnanimusGroup selectedGroup = groupQueryAdapter.getItem(position);
                String groupID = selectedGroup.getObjectId();
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                intent.putExtra("objID", groupID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doListQuery();
    }

    private void doListQuery() {
        groupQueryAdapter.loadObjects();
    }

    private ParseQueryAdapter<UnanimusGroup> groupQueryAdapter;
}
