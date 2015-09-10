package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONException;
import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;

/**
 * This class shows the groups a user is a part of, as well as allows the
 * user to access the make and join group_activity activities.
 */
public class MainActivity extends UnanimusActivityTitle {
    //    private final GroupQueryWorker groupQueryWorker = new GroupQueryWorker();
    public MainActivity() {
        super("ma");
        factory = new ParseQueryAdapter.QueryFactory<UnanimusGroup>() {
            @Override
            public ParseQuery<UnanimusGroup> create() {
                ParseQuery<UnanimusGroup> query = ParseQuery.getQuery(UnanimusGroup.class);
                query.orderByDescending("createdAt");
                query.whereEqualTo("userIds", ParseUser.getCurrentUser().getObjectId());
                return query;
            }
        };
    }

    private final ParseQueryAdapter.QueryFactory<UnanimusGroup> factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        try {
            setTitleBar(R.string.ma_title, (ViewGroup) findViewById(R.id.main_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        final String facebookId = ParseUser
                .getCurrentUser()
                .getString(FriendPickerActivity.FACEBOOK_ID);

        //Facebook Picture
        final ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.ma_prof_pic);
        profilePictureView.setProfileId(facebookId);

        final GraphRequest graphRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + facebookId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(final GraphResponse graphResponse) {
                        final FacebookRequestError fre = graphResponse.getError();
                        if (fre != null) {
                            final FacebookException fe = fre.getException();
                            final String freMsg = fre.getErrorMessage();

                            log(ELog.e, freMsg, fe);

                            return;
                        }

                        final JSONObject graphObject = graphResponse.getJSONObject();
                        final String name;
                        try {
                            name = graphObject.getString("name");
                        } catch (JSONException e) {
                            log(ELog.e, e.getMessage(), e);
                            return;
                        }

                        final TextView nameView = (TextView) findViewById(R.id.ma_facebook_name);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nameView.setText(name);
                            }
                        });
                    }
                });

        final GraphRequest[] requests = new GraphRequest[] {
                graphRequest
        };

        final GraphRequestBatch graphRequestBatch = new GraphRequestBatch(requests);

        graphRequestBatch.executeAsync();
    }

    private void doQuery(ParseQueryAdapter.QueryFactory<UnanimusGroup> factory) {
        factory.create().getFirstInBackground().continueWith(new Continuation<UnanimusGroup, Void>() {
            @Override
            public Void then(Task<UnanimusGroup> task) throws Exception {
                UnanimusGroup unanimusGroup = task.getResult();
                unanimusGroup.load();

                final String objectId = unanimusGroup.getObjectId();

                final ProfilePictureView groupPpv = (ProfilePictureView) findViewById(R.id.uga_invited_by);
                final TextView textView = (TextView) findViewById(R.id.uga_groupID_view);

                String ownerId = unanimusGroup.getCgaContainer().getOwnerId();

                ParseUser owner = ParseQuery.getQuery(ParseUser.class).whereEqualTo(ParseCache.OBJECT_ID, ownerId).getFirst();

                final String facebookId = owner.getString(FriendPickerActivity.FACEBOOK_ID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupPpv.setProfileId(facebookId);

                        View view = findViewById(R.id.unanimus_group_abstract);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                                intent.putExtra(ParseCache.OBJECT_ID, objectId);
                                startActivity(intent);
                                onPause();
                            }
                        });
                    }
                });

                final GraphRequest graphRequest = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + facebookId,
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(final GraphResponse graphResponse) {
                                FacebookRequestError fbe = graphResponse.getError();
                                if (fbe != null) {
                                    log(ELog.e, fbe.getErrorMessage(), fbe.getException());
                                    throw fbe.getException();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final String name;
                                        try {
                                            name = graphResponse.getJSONObject().getString("name");
                                        } catch (JSONException e) {
                                            log(ELog.e, e.getMessage(), e);
                                            return;
                                        }
                                        textView.setText(name);
                                    }
                                });
                            }
                        }
                );

                GraphRequest[] graphRequests = new GraphRequest[] { graphRequest };
                GraphRequestBatch requestBatch = new GraphRequestBatch(graphRequests);
                requestBatch.executeAndWait();

                return null;
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
        onPause();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Shows all the groups user is a member of
        doQuery(factory);
    }

    public void ma_viewDoQuery(View view) {
        doQuery(factory);
    }
}
