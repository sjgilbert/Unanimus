package com.sjgilbert.unanimus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is the friend picker activity which allows the user to
 * select the friends who will be part of the group.
 * <p/>
 * JSON adapter solution found at http://stackoverflow.com/questions/6277154/populate-listview-from-json
 * Custom ArrayList adapter idea for ListView fount at http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */
public class FriendPickerActivity extends UnanimusActivityTitle {
    static final String FPA = "FpaContainer";
    private static final String tag = "fpa";
    private final FpaContainer fpaContainer = new FpaContainer();
    private final ArrayList<String> facebookUserIds = new ArrayList<>();
    private final String userFacebookID = ParseUser.getCurrentUser().getString("facebookID");


    public FriendPickerActivity() {
        super(tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_picker_activity);
        try {
            setTitleBar(R.string.fpa_title, (ViewGroup) findViewById(R.id.friend_picker_activity).findViewById(R.id.fpa_title_bar));
        } catch (NullPointerException | ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        facebookUserIds.add(userFacebookID);
        fpaContainer.setDefault();

        //The request for facebook friends
        GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray friends, GraphResponse response) {
                        if (response.getError() != null) {
                            log(ELog.e, response.getError().toString());
                            return;
                        }

                        int length = friends.length();
                        List<String> names = new ArrayList<>(length);
                        final List<String> ids = new ArrayList<>(length);

                        try {
                            for (int i = 0; i < length; ++i) {
                                names.add(friends.getJSONObject(i).getString("name"));
                                ids.add(friends.getJSONObject(i).getString("id"));
                            }
                        } catch (JSONException e) {
                            log(ELog.e, e.getMessage(), e);
                            return;
                        }

                        ListView friendListView = (ListView) findViewById(R.id.fpa_list_view);

                        friendListView.setAdapter(
                                new FriendPickerListAdapter(
                                        FriendPickerActivity.this,
                                        names,
                                        ids
                                )
                        );

                        friendListView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id
                                    ) {
                                        final String idp = ids.get(position);
                                        final int color;
                                        if (facebookUserIds.contains(idp)) {
                                            removeFacebookID(idp);
                                            color = Color.TRANSPARENT;
                                        } else {
                                            addFacebookID(idp);
                                            color = Color.argb(127, 0, 0, 255);
                                        }

                                        view.setBackgroundColor(color);
                                    }
                                }
                        );
                    }
                }
        ).executeAsync();

        Button doneButton = (Button) findViewById(R.id.fpa_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 >= facebookUserIds.size()) {
                    final boolean[] doFinish = new boolean[]{false};
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendPickerActivity.this);

                    builder
                            .setMessage("No friends selected!  Continue anyway?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    doFinish[0] = true;
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    doFinish[0] = false;
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();

                    if (!doFinish[0])
                        return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(FriendPickerActivity.this);
                final Handler updateBarHandler = new Handler();

                progressDialog.setTitle("Getting member information");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);
                progressDialog.setMax(FriendPickerActivity.this.facebookUserIds.size());
                progressDialog.show();

                new GetUserIdsPairsWorker(
                        FriendPickerActivity.this.facebookUserIds.toArray(
                                new String[FriendPickerActivity.this.facebookUserIds.size()]
                        )
                ) {
                    private final ProgressDialog dialog = progressDialog;
                    private final Handler handler = updateBarHandler;

                    @Override
                    protected void onProgressUpdate(Integer... progress) {
                        super.onProgressUpdate(progress);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setProgress(getCompletedQueries());
                            }
                        });
                    }

                    @Override
                    protected void onPostExecute(FpaContainer.UserIdPair[] result) {
                        FriendPickerActivity.this.fpaContainer.userIdPairs = result;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });

                        finish();
                    }
                }.execute();
            }
        });
    }

    private void addFacebookID(String friendID) {
        facebookUserIds.add(friendID);
    }

    private void removeFacebookID(String friendID) {
        facebookUserIds.remove(friendID);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        int result;
        if (fpaContainer.isSet()) {
            intent.putExtra(FPA, fpaContainer.getAsBundle());
            result = RESULT_OK;
        } else {
            result = RESULT_CANCELED;
        }
        setResult(result, intent);

        super.finish();
    }

    public static class FpaContainer extends CreateGroupActivity.ADependencyContainer {
        private final static String FACEBOOK_IDS = "FacebookIds";
        private final static String PARSE_IDS = "ParseIds";
        private UserIdPair[] userIdPairs;

        @Override
        boolean isSet() {
            return (1 < userIdPairs.length);
        }

        @Override
        Bundle getAsBundle() {
            Bundle bundle = new Bundle();

            final ArrayList<String> facebookIds = new ArrayList<>();
            final ArrayList<String> parseIds = new ArrayList<>();

            for (UserIdPair pair : userIdPairs) {
                if (null == pair.parseUserId) {
                    Log.w(
                            "Unanimus/" + tag,
                            "Missing parse user id in a selected friend, skipping . . . "
                    );

                    continue;
                }

                facebookIds.add(pair.facebookUserId);
                parseIds.add(pair.parseUserId);
            }

            bundle.putStringArrayList(FACEBOOK_IDS, facebookIds);
            bundle.putStringArrayList(PARSE_IDS, parseIds);

            try {
                super.getAsBundle();
            } catch (NotSetException e) {
                Log.e("Unanimus", e.getMessage(), e);
                return null;
            }

            return bundle;
        }

        @Override
        void setDefault() {
            final String userFacebookID = ParseUser.getCurrentUser().getString("facebookID");
            final String userParseId = ParseUser.getCurrentUser().getObjectId();
            userIdPairs = new UserIdPair[]{new UserIdPair(userFacebookID, userParseId)};
        }

        @Override
        void setFromBundle(Bundle bundle) {
            final ArrayList<String> facebookIds = bundle.getStringArrayList(FACEBOOK_IDS);
            final ArrayList<String> parseUserIds = bundle.getStringArrayList(PARSE_IDS);

            assert parseUserIds != null;
            assert facebookIds != null;

            if (parseUserIds.size() != facebookIds.size())
                throw new IllegalArgumentException();

            userIdPairs = new UserIdPair[facebookIds.size()];

            for (int i = 0; userIdPairs.length > i; ++i)
                userIdPairs[i] = new UserIdPair(facebookIds.get(i), parseUserIds.get(i));
        }

        public static class UserIdPair {
            public final String facebookUserId;
            public final String parseUserId;

            private UserIdPair(String facebookUserId, String parseUserId) {
                this.facebookUserId = facebookUserId;
                this.parseUserId = parseUserId;
            }
        }
    }

    private class GetUserIdsPairsWorker extends AsyncTask<String[], Integer, FpaContainer.UserIdPair[]> {
        final int maxThreads = 2 * Runtime.getRuntime().availableProcessors() + 1;
        private final long waitTimeDenominator = 100L;
        private final String facebookIdKey = getString(IntroPageActivity.facebookID);
        private final AtomicInteger runningQueries = new AtomicInteger(0);
        private final AtomicInteger completedQueries = new AtomicInteger(0);
        private final AtomicInteger canceledQueries = new AtomicInteger(0);
        private final String[] facebookUserIds;
        private final FpaContainer.UserIdPair[] userIdPairs;

        public GetUserIdsPairsWorker(String[] facebookUserIds) {
            this.facebookUserIds = facebookUserIds;
            this.userIdPairs = new FpaContainer.UserIdPair[facebookUserIds.length];

            log(
                    ELog.i,
                    String.format(
                            Locale.getDefault(),
                            "%s: %d",
                            "Initialized GetUserIdsPairsWorker with max threads",
                            getMaxThreads()
                    )
            );
        }

        int getMaxThreads() {
            return maxThreads;
        }

        int getRunningQueries() {
            return runningQueries.get();
        }

        int getCompletedQueries() {
            return completedQueries.get();
        }

        int getCanceledQueries() {
            return canceledQueries.get();
        }

        @Override
        protected FpaContainer.UserIdPair[] doInBackground(String[]... params) {
            for (int i = 0; userIdPairs.length > i; ++i) {
                final int finalI = i;

                ParseQuery<ParseUser> parseUserQuery = ParseQuery.getQuery(ParseUser.class)
                        .whereEqualTo(facebookIdKey, facebookUserIds[i]);

                runningQueries.incrementAndGet();

                parseUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    private final int i = finalI;
                    private final FpaContainer.UserIdPair[] userIdPairs = GetUserIdsPairsWorker.this.userIdPairs;
                    private final String facebookId = GetUserIdsPairsWorker.this.facebookUserIds[i];

                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        runningQueries.decrementAndGet();

                        final String puId;
                        final AtomicInteger integer;

                        if (null != e) {
                            log(
                                    ELog.e,
                                    e.getMessage(),
                                    e
                            );

                            puId = null;
                            integer = canceledQueries;
                        } else {
                            puId = parseUser.getObjectId();
                            integer = completedQueries;
                        }

                        userIdPairs[i] = new FpaContainer.UserIdPair(facebookId, puId);

                        integer.incrementAndGet();
                        publishProgress();
                    }
                });

                while (maxThreads <= runningQueries.get()) {
                    try {
                        Thread.sleep(waitTimeDenominator / (long) runningQueries.get());
                    } catch (InterruptedException e) {
                        log(
                                ELog.e,
                                e.getMessage(),
                                e
                        );
                    }
                }
            }

            while (0 != runningQueries.get()) {
                try {
                    Thread.sleep(waitTimeDenominator * (long) runningQueries.get());
                } catch (InterruptedException e) {
                    log(
                            ELog.e,
                            e.getMessage(),
                            e
                    );
                }
            }

            return userIdPairs;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            log(
                    ELog.i,
                    String.format(
                            Locale.getDefault(),
                            "%s\n%s: %d\n%s: %d\n%s: %d",
                            "Fetching Parse-User ids from Facebook-User ids",
                            "Running Queries",
                            getRunningQueries(),
                            "Completed Queries",
                            getCompletedQueries(),
                            "Canceled Queries",
                            getCanceledQueries()
                    )
            );
        }
    }
}
