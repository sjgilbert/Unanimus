package com.sjgilbert.unanimus;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONException;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;

/**
 * The activity which displays a specific group_activity a user is a part of.  Should
 * eventually allow the user to indicate preferences/view recommendations.
 */
public class GroupActivity extends UnanimusActivityTitle {
    public static final String GROUP_ID = "GROUP_ID";
    private static final String TAG = "ga";
    private String unanimusGroupId;
    private UnanimusGroup unanimusGroup;

    public GroupActivity() {
        super(TAG);
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
            unanimusGroupId = extras.getString(ParseCache.OBJECT_ID);
        } else {
            throw new IllegalArgumentException();
        }

        assert unanimusGroupId != null;

        //Query for the group_activity's data
        ParseQuery query = ParseQuery.getQuery(UnanimusGroup.class);
        query.whereEqualTo(ParseCache.OBJECT_ID, unanimusGroupId);

        ParseCache.parseCache.put(unanimusGroupId, (ParseQuery<ParseObject>) query);

        ((ParseQuery<UnanimusGroup>) query).getFirstInBackground().onSuccess(new Continuation<UnanimusGroup, UnanimusGroup>() {
            @Override
            public UnanimusGroup then(Task<UnanimusGroup> task) throws Exception {
                final UnanimusGroup unanimusGroup = task.getResult();
                unanimusGroup.load();

                GroupActivity.this.unanimusGroup = unanimusGroup;

                CgaContainer cgaContainer = unanimusGroup.getCgaContainer();
                FpaContainer fpaContainer = cgaContainer.getFpaContainer();

                assert fpaContainer != null;
                final FpaContainer.UserIdPair[] userIdPairs = fpaContainer.getUserIdPairs();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(userIdPairs);
                    }
                });

                return null;
            }
        }).onSuccessTask(new Continuation<UnanimusGroup, Task<Void>>() {
            @Override
            public Task<Void> then(Task<UnanimusGroup> task) throws Exception {
                return refreshButtons(unanimusGroup);
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Exception e = task.getError();
                if (e != null)
                    log(ELog.e, e.getMessage(), e);
                else
                    log(ELog.i, "Successfully created");

                return null;
            }
        });
    }

    private void setData(final FpaContainer.UserIdPair[] userIdPairs) {
        final int length = userIdPairs.length;

        final ArrayList<String> usernames = new ArrayList<>(length);
        GraphRequest[] requests = new GraphRequest[length];
        for (int i = 0; i < length; i++) {
            String user = userIdPairs[i].facebookUserId;
            requests[i] = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    String.format("/%s", user),
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            if (response.getError() != null) {
                                log(ELog.e, response.getError().getErrorMessage());
                                return;
                            }
                            final String userName;
                            try {
                                userName = response.getJSONObject().getString("name");
                            } catch (JSONException e) {
                                log(ELog.e, e.getMessage(), e);
                                return;
                            }

                            usernames.add(userName);

                            //Setting owner of group_activity
                            TextView createdBy = (TextView) findViewById(R.id.ga_created_by);
                            createdBy.setText("Created by: " + usernames.get(0));
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
                membersList.setScrollContainer(false);
            }
        });

        requestBatch.executeAsync();

    }
    @SuppressWarnings("unused")
    public void ga_viewStartVotingActivity(@SuppressWarnings("UnusedParameters") View view) {
        Intent intent = new Intent(GroupActivity.this, VotingActivity.class);
        intent.putExtra(ParseCache.OBJECT_ID, unanimusGroupId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (unanimusGroup != null)
            refreshButtons(unanimusGroup);
    }

    private Task<Void> refreshButtons(UnanimusGroup unanimusGroup) {
        return unanimusGroup.fetchInBackground().onSuccess(new Continuation<ParseObject, UnanimusGroup>() {
            @Override
            public UnanimusGroup then(Task<ParseObject> task) throws Exception {
                final ParseObject result = task.getResult();
                final UnanimusGroup unanimusGroup = (UnanimusGroup) result;

                GroupActivity.this.unanimusGroup = unanimusGroup;

                unanimusGroup.load();

                return unanimusGroup;
            }
        }).onSuccess(new Continuation<UnanimusGroup, Void>() {
            @Override
            public Void then(Task<UnanimusGroup> task) throws Exception {
                final UnanimusGroup unanimusGroup = task.getResult();

                final Button playButton = (Button) findViewById(R.id.ga_play_button);
                final Button recsButton = (Button) findViewById(R.id.ga_view_recs_button);

                final boolean playable = unanimusGroup.hasNotVoted();
                final boolean recsReady = unanimusGroup.allHaveVoted();

                final Resources resources = getResources();

                final int buttonColor = resources.getColor(R.color.button_color);
                final int shadedColor = resources.getColor(R.color.button_shaded);

                final int playColor = (playable) ? buttonColor : shadedColor;
                final int recsColor = (recsReady) ? buttonColor : shadedColor;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playButton.setBackgroundColor(playColor);
                        playButton.setClickable(playable);

                        recsButton.setBackgroundColor(recsColor);
                        recsButton.setClickable(recsReady);
                    }
                });
                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Exception e = task.getError();
                if (e != null)
                    log(ELog.e, e.getMessage(), e);
                else
                    log(ELog.i, "Successfully resumed");

                return null;
            }
        });
    }

    @SuppressWarnings("unused")
    public void ga_viewStartRecommendationActivity(@SuppressWarnings("UnusedParameters") View view) {
        if (unanimusGroup.getRecommendation().isEmpty())
            return;

        Intent intent = new Intent(GroupActivity.this, RecommendationActivity.class);
        intent.putExtra(GROUP_ID, unanimusGroupId);
        startActivity(intent);
        onPause();
    }
}
