package com.sjgilbert.unanimus;

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
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import static com.sjgilbert.unanimus.CreateGroupActivity.*;

/**
 * This class shows the groups a user is a part of, as well as allows the
 * user to access the make and join group_activity activities.
 */
public class MainActivity extends UnanimusActivityTitle {
    private ParseQueryAdapter<UnanimusGroup> groupQueryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        try {
            setTitleBar(R.string.ma_title, (ViewGroup) findViewById(R.id.main_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        //Button to join group_activity
        final Button joinGroupButton = (Button) findViewById(R.id.ma_join_group);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
                startActivity(intent);
            }
        });

        //Facebook Picture
        ProfilePictureView profpic = (ProfilePictureView) findViewById(R.id.ma_prof_pic);
        profpic.setProfileId((String) ParseUser.getCurrentUser().get("facebookID"));

        //Button to make group_activity
        Button makeGroupButton = (Button) findViewById(R.id.ma_make_group_button);
        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakeGroupActivity.class);
                startActivity(intent);
            }
        });

        //Button to access friend picker
        Button friendPickerButton = (Button) findViewById(R.id.ma_friend_picker_button);
        friendPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendPickerActivity.class);
                startActivity(intent);
            }
        });

        //Button for Group Settings Picker
        Button groupSettingsPickerButton = (Button) findViewById(R.id.ma_group_settings_picker_button);
        groupSettingsPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroupSettingsPickerActivity.class);
                startActivity(intent);
            }
        });

        //Shows all the groups user is a member of
        ParseQueryAdapter.QueryFactory<UnanimusGroup> factory =
                new ParseQueryAdapter.QueryFactory<UnanimusGroup>() {
                    public ParseQuery<UnanimusGroup> create() {
                        ParseQuery<UnanimusGroup> query = UnanimusGroup.getQuery();
                        query.include("objectID");
                        query.whereEqualTo("user", ParseUser.getCurrentUser());
                        query.orderByDescending("createdAt");
                        return query;
                    }
                };

        groupQueryAdapter = new ParseQueryAdapter<UnanimusGroup>(this, factory) {
            @Override
            public View getItemView(UnanimusGroup group, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.unanimus_group_abstract, null);
                }
                TextView groupView = (TextView) view.findViewById(R.id.uga_groupID_view);
                try {
                    groupView.setText(group.getObjectId());
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
                final UnanimusGroup selectedGroup = groupQueryAdapter.getItem(position);
                String groupID = selectedGroup.getObjectId();
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                intent.putExtra("objID", groupID);
                startActivity(intent);
            }
        });
    }

    public void startPlacePickActivity(View view) {
        startActivity(new Intent(MainActivity.this, PlacePickActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        doListQuery();
    }

    private void doListQuery() {
        groupQueryAdapter.loadObjects();
    }
}
