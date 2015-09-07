package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The activity for voting on restaurantIterator
 */
public class VotingActivity
        extends UnanimusActivityTitle
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String VA = "va";

    private final BuildGoogleApiClientWorker googleApiClientWorker
            = new BuildGoogleApiClientWorker(this);
    private final AtomicInteger groupIsLoaded = new AtomicInteger(0);
    private final AtomicInteger isSynchronouslyExecuting = new AtomicInteger(0);
    private final AtomicInteger startedRestaurantSearch = new AtomicInteger(0);
    private GoogleApiClient googleApiClient = null;
    private String groupKey;
    private Iterator<Place> placeIterator;
    private PlaceBuffer placeBuffer;
    private int i;
    private TextView counter;
    private List<String> restaurantIds;
    private UnanimusGroup unanimusGroup;

    public VotingActivity() {
        super(VA);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voting_activity);

        try {
            setTitleBar(R.string.voting_activity_title, (ViewGroup) findViewById(R.id.voting_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        Bundle extras = getIntent().getExtras();    //The GROUP_ID of the selected group_activity
        if (extras != null) {
            this.groupKey = extras.getString(ParseCache.OBJECT_ID);

            ParseQuery parseQuery = ParseCache.parseCache.get(groupKey);

            if (parseQuery == null) {
                parseQuery = UnanimusGroup.getQuery()
                        .whereEqualTo(ParseCache.OBJECT_ID, groupKey);

                ParseCache.parseCache.put(groupKey, (ParseQuery<ParseObject>) parseQuery);
            }

            ((ParseQuery<UnanimusGroup>) parseQuery).getFirstInBackground(new GroupQueryGetCallback());
        } else {
            log(ELog.w, "Extras null, this is treated as an illegal argument exception.");
            throw new IllegalArgumentException();
        }

        this.counter = (TextView) findViewById(R.id.va_voting_counter);

        googleApiClientWorker.execute();
    }

    @Override
    protected void onStop() {
        placeBuffer.release();
        super.onStop();
    }

    @Override
    public void finish() {
        if (unanimusGroup == null) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK);

        super.finish();
    }

    private void checkDependenciesFulfilled() {
        executeSynchronous(new Runnable() {
            @Override
            public void run() {
                if (googleApiClientWorker.getStatus() == AsyncTask.Status.FINISHED
                        && googleApiClient.isConnected()
                        && unanimusGroup != null
                        && groupIsLoaded.get() != 0
                        && startedRestaurantSearch.getAndIncrement() == 0)
                    setPlaces();
            }
        });
    }

    private void setPlaces() {
        Places.GeoDataApi.getPlaceById(
                googleApiClient,
                restaurantIds.toArray(
                        new String[ restaurantIds.size() ]))
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            placeBuffer = places;
                            placeIterator = places.iterator();

                            if (placeIterator.hasNext())
                                setRestaurantView(placeIterator.next());

                            for (Place place : places)
                                log(ELog.i, "Place found: " + place.getName());
                        } else log(ELog.e, "Places not found");
                    }
                });
    }

    private synchronized void executeSynchronous(Runnable runnable) {
        isSynchronouslyExecuting.getAndIncrement();

        runnable.run();

        isSynchronouslyExecuting.getAndDecrement();
    }

    private void setYesVote(int index) {
        unanimusGroup.vote(index, VotesList.getUpVote(), null);
    }

    private void setNoVote(int index) {
        unanimusGroup.vote(index, VotesList.getDownVote(), null);
    }

    private void incrementRestaurant() {
        setRestaurantView(placeIterator.next());
        i++;
        counter.setText(String.format("%d/%d", i + 1, restaurantIds.size()));
    }

    public void va_viewVoteNo(View view) {
        setNoVote(i);
        if (placeIterator.hasNext()) {
            incrementRestaurant();
        } else {
            placeBuffer.release();
            finish();
        }
    }

    public void va_viewVoteYes(View view) {
        setYesVote(i);
        if (placeIterator.hasNext()) {
            incrementRestaurant();
        } else {
            placeBuffer.release();
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkDependenciesFulfilled();
    }

    private void setRestaurantView(Place place) {
        TextView textView = (TextView) findViewById(R.id.va_voting_restaurant_view);
        textView.setText(place.getName() + "\n" + place.getAddress().toString().split(",")[0] + "\n" + place.getPhoneNumber().toString().split(" ")[1]);
    }

    @Override
    public void onConnectionSuspended(int i) {
        log(ELog.w, "Google Places Api client connection was suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log(ELog.w, connectionResult.toString());
    }

    private void setGoogleApiClient(BuildGoogleApiClientWorker buildGoogleApiClientAsyncTask) {
        try {
            googleApiClient = buildGoogleApiClientAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            log(ELog.e, e.getMessage(), e);
            return;
        }
        googleApiClient.connect();
    }

    private static abstract class VotingActivityAsyncTask<T1, T2, T3>
            extends AsyncTask<T1, T2, T3> {
        private final VotingActivity votingActivity;

        public VotingActivityAsyncTask(VotingActivity votingActivity) {
            super();
            this.votingActivity = votingActivity;
        }
    }

    private static class BuildGoogleApiClientWorker
            extends VotingActivityAsyncTask<Object, Object, GoogleApiClient> {
        public BuildGoogleApiClientWorker(VotingActivity votingActivity) {
            super(votingActivity);
        }

        private static GoogleApiClient buildGoogleApiClient(
                Context context,
                GoogleApiClient.ConnectionCallbacks callbacks,
                GoogleApiClient.OnConnectionFailedListener connectionFailedListener
        ) {
            return new GoogleApiClient
                    .Builder(context)
                    .addConnectionCallbacks(callbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        @Override
        protected GoogleApiClient doInBackground(Object... params) {
            return buildGoogleApiClient(
                    super.votingActivity,
                    super.votingActivity,
                    super.votingActivity
            );
        }

        @Override
        protected void onPostExecute(GoogleApiClient googleApiClient) {
            super.votingActivity.setGoogleApiClient(this);
        }

    }

    private class GroupQueryGetCallback implements GetCallback<UnanimusGroup> {
        @Override
        public void done(UnanimusGroup unanimusGroup, ParseException e) {
            if (e != null) {
                log(ELog.e, e.getMessage(), e);
                finish();
                return;
            }

            try {
                unanimusGroup.load();
            } catch (ParseException e1) {
                log(ELog.e, e1.getMessage(), e1);
                finish();
                return;
            }

            VotingActivity.this.unanimusGroup = unanimusGroup;

            VotingActivity.this.restaurantIds = unanimusGroup.getRestaurantIds();

            groupIsLoaded.getAndIncrement();

            checkDependenciesFulfilled();
        }
    }
}
