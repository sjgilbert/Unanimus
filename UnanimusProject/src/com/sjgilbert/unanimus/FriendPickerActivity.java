package com.sjgilbert.unanimus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import org.json.JSONObject;

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
    public static final String FACEBOOK_ID = "facebookID";
    static final String FPA = "fpaContainer";
    static final String TAG = "fpa";
    private final FpaContainer fpaContainer = new FpaContainer();

    private FriendPickerListAdapter friendPickerListAdapter;

    public FriendPickerActivity() {
        super(TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_picker_activity);
        try {
            setTitleBar(
                    R.string.fpa_title,
                    (ViewGroup) findViewById(R.id.fpa_title_bar)
            );
        } catch (NullPointerException | ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        fpaContainer.setDefault();

        //The request for facebook friends
        GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new CustomGraphJSONArrayCallback()
        ).executeAsync();

        Button doneButton = (Button) findViewById(R.id.fpa_done_button);
        doneButton.setOnClickListener(new DoneButtonOnClickListener());
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        int result;
        if (fpaContainer.isSet()) {
            try {
                intent.putExtra(FPA, fpaContainer.getAsBundle());
            } catch (IContainer.NotSetException e) {
                log(ELog.e, e.getMessage(), e);
            }
            result = RESULT_OK;
        } else {
            result = RESULT_CANCELED;
        }
        setResult(result, intent);

        super.finish();
    }

    private FacebookId[] getSelectedFacebookIds() {
        return friendPickerListAdapter.getSelectedFacebookIds();
    }

    private void startFinish(FacebookId[] facebookIds) {
        final ProgressDialog progressDialog = new ProgressDialog(FriendPickerActivity.this);
        final Handler updateBarHandler = new Handler();

        progressDialog.setTitle("Getting member information");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(FriendPickerActivity.this.getSelectedFacebookIds().length);
        progressDialog.show();

        new GetUserIdsPairsWorker(facebookIds) {
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
                FriendPickerActivity.this.fpaContainer.setUserIdPairs(result);

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log(ELog.e, e.getMessage(), e);
                }

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

    private class DoneButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final FacebookId[] facebookIds = FriendPickerActivity.this.getSelectedFacebookIds();

            if (1 < facebookIds.length) {
                startFinish(facebookIds);
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(FriendPickerActivity.this);

            builder
                    .setMessage("No friends selected!  Continue anyway?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startFinish(facebookIds);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @SuppressWarnings("unused")
    private final class CustomOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(
                AdapterView<?> adapterView,
                View view,
                int position,
                long id
        ) {
            final FriendPickerListAdapter.ViewHolder viewHolder
                    = (FriendPickerListAdapter.ViewHolder) view.getTag();

            viewHolder.toggleSelected();
        }
    }

    @SuppressWarnings("unused")
    private final class CustomGraphJSONArrayCallback
            implements GraphRequest.GraphJSONArrayCallback {
        @Override
        public void onCompleted(JSONArray friends, GraphResponse response) {
            if (response.getError() != null) {
                log(ELog.e, response.getError().toString());
                return;
            }

            int length = friends.length();
            final List<String> allFriendNames = new ArrayList<>(length);
            final List<FacebookId> allFacebookIds = new ArrayList<>(length);

            for (int i = 0; i < length; ++i) {
                final String nameKey = "name";
                final String idKey = "id";

                final JSONObject jsonObject;
                final String name;
                final String id;
                try {
                    jsonObject = friends.getJSONObject(i);
                    name = jsonObject.getString(nameKey);
                    id = jsonObject.getString(idKey);
                } catch (JSONException e) {
                    log(ELog.e, e.getMessage(), e);
                    return;
                }
                allFriendNames.add(name);
                allFacebookIds.add(new FacebookId(id));
            }

            ListView friendListView = (ListView) findViewById(R.id.fpa_list_view);

            FriendPickerActivity.this.friendPickerListAdapter = new FriendPickerListAdapter(
                    FriendPickerActivity.this,
                    allFriendNames,
                    allFacebookIds
            );

            friendListView.setAdapter(FriendPickerActivity.this.friendPickerListAdapter);

            AdapterView.OnItemClickListener onItemClickListener
                    = new CustomOnItemClickListener();

            friendListView.setOnItemClickListener(onItemClickListener);
        }
    }


    private class GetUserIdsPairsWorker
            extends AsyncTask<String[], Integer, FpaContainer.UserIdPair[]> {
        final int maxThreads = 2 * Runtime.getRuntime().availableProcessors() + 1;
        private final long waitTimeDenominator = 100L;
        private final String facebookIdKey = getString(IntroPageActivity.facebookID);
        private final AtomicInteger runningQueries = new AtomicInteger(0);
        private final AtomicInteger completedQueries = new AtomicInteger(0);
        private final AtomicInteger canceledQueries = new AtomicInteger(0);
        private final FacebookId[] facebookUserIds;
        private final FpaContainer.UserIdPair[] userIdPairs;

        public GetUserIdsPairsWorker(FacebookId[] facebookUserIds) {
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
                        .whereEqualTo(facebookIdKey, facebookUserIds[i].toString());

                runningQueries.incrementAndGet();

                parseUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    private final int i = finalI;
                    private final FpaContainer.UserIdPair[] userIdPairs = GetUserIdsPairsWorker.this.userIdPairs;
                    private final String facebookId
                            = GetUserIdsPairsWorker.this.facebookUserIds[i].toString();

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

                        userIdPairs[i] = new FpaContainer.UserIdPair(puId, facebookId);

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
