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
import com.parse.ParseException;
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

        ListView groupListView = (ListView) findViewById(R.id.ma_groups_list_view);

        groupQueryAdapter = new GroupQueryAdapter(
                groupListView.getContext(),
                factory,
                R.layout.unanimus_group_abstract
        );

        groupQueryAdapter.setAutoload(false);

        groupListView.setAdapter(groupQueryAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CgaContainer selectedGroup = groupQueryAdapter.getItem(position);
                try {
                    selectedGroup.load();
                } catch (ParseException e) {
                    log(ELog.e, e.getMessage(), e);
                }
                String groupID = selectedGroup.getObjectId();
                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                intent.putExtra(ParseCache.OBJECT_ID, groupID);
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
