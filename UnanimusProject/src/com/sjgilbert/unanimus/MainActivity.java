package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.QueryFactory;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.Locale;

import static com.sjgilbert.unanimus.CreateGroupActivity.CgaGroup;

/**
 * This class shows the groups a user is a part of, as well as allows the
 * user to access the make and join group_activity activities.
 */
public class MainActivity extends UnanimusActivityTitle {
    private final GroupQueryWorker groupQueryWorker = new GroupQueryWorker();
    private ParseQueryAdapter<CgaGroup> groupQueryAdapter;

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
        profilePictureView.setProfileId((String) ParseUser.getCurrentUser().get("facebookID"));

        groupQueryWorker.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AsyncTask.Status.FINISHED == groupQueryWorker.getStatus()) {
            doListQuery();
            log(
                    ELog.i,
                    "Resumed successfully."
            );
        } else {
            log(
                    ELog.d,
                    String.format(
                            Locale.getDefault(),
                            "%s.  %s: %s",
                            "Resumed but group query worker not finished",
                            "Status",
                            groupQueryWorker.getStatus().toString()
                    )
            );
        }
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void ma_viewFriendPicker(View view) {
        startActivity(
                new Intent(
                        this,
                        FriendPickerActivity.class
                )
        );
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void ma_viewPlacePick(View view) {
        startActivity(
                new Intent(
                        this,
                        PlacePickActivity.class
                )
        );
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void ma_viewGroupSettingsPicker(View view) {
        startActivity(
                new Intent(
                        this, GroupSettingsPickerActivity.class
                )
        );
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

    private class GroupQueryWorker extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            //Shows all the groups user is a member of
            QueryFactory<CgaGroup> factory = new QueryFactory<CgaGroup>() {
                public ParseQuery<CgaGroup> create() {
                    ParseQuery<CgaGroup> query = CgaGroup.getQuery();
                    query.include("objectID");
                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                    query.orderByDescending("createdAt");
                    return query;
                }
            };

            groupQueryAdapter = new ParseQueryAdapter<CgaGroup>(MainActivity.this, factory) {
                @Override
                public View getItemView(CgaGroup group, View view, ViewGroup parent) {
                    if (view == null) {
                        view = View.inflate(getContext(), R.layout.unanimus_group_abstract, null);
                    }
                    TextView groupView = (TextView) view.findViewById(R.id.uga_groupID_view);
                    try {
                        groupView.setText(group.getObjectId());
                    } catch (NullPointerException e) {
                        log(ELog.e, e.getMessage(), e);
                    }
                    return view;
                }
            };

            groupQueryAdapter.setAutoload(false);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            doListQuery();
        }
    }
}
