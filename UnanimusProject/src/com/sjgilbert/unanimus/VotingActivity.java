package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

/**
 * The activity for voting on restaurantIterator
 */
public class VotingActivity
        extends UnanimusActivityTitle
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int NUMBER_OF_RESTAURANTS = 15;

    private static final String VA = "va";

    private final BuildGoogleApiClientWorker googleApiClientWorker
            = new BuildGoogleApiClientWorker(this);

    private GoogleApiClient googleApiClient = null;

    private final CgaContainer cgaContainer = new CgaContainer();

    private UnanimusGroup group;
    private String groupKey;

    private Iterator<Place> placeIterator;
    private PlaceBuffer placeBuffer;

    private int i;
    private TextView counter;
    private ListIterator<String> restaurantIterator;

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
            cgaContainer.setFromBundle(extras.getBundle("UnanimusGroup"));
            UnanimusGroup.Builder builder = new UnanimusGroup.Builder(cgaContainer);
            builder.getInBackground(new UnanimusGroup.Builder.Callback() {
                @Override
                public void done(UnanimusGroup unanimusGroup) {
                    group = unanimusGroup;
                    restaurantIterator = group.getRestaurantIterator();

                    final TextView restaurant = (TextView) findViewById(R.id.va_voting_restaurant_view);

                    while (restaurantIterator.hasNext()) {
                        String restaurantId = restaurantIterator.next();
                        restaurant.setText(restaurantId);
                    }
                }
            });
//            groupKey = extras.getString(GroupActivity.GROUP_ID);
        } else {
            Toast.makeText(VotingActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

//        ParseQuery<ParseObject> query = ParseCache.parseKey);
//        if (query == null) {
//            log(ELog.e, "messed up");
//            finish();
//        }
//
//        assert query != null;
//
//        try {
//            group = (UnanimusGroup) query.getFirst();
//        } catch (ClassCastException | ParseException e) {
//            log(ELog.e, e.getMessage(), e);
//            finish();
//        }

        counter = (TextView) findViewById(R.id.va_voting_counter);

        googleApiClientWorker.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        if (group == null) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK);

        super.finish();
    }

    private void setYesVote(int index) {
        group.vote(index, Vote.getUpVote(), null);
    }

    private void setNoVote(int index) {group.vote(index, Vote.getDownVote(), null);
    }

    private void incrementRestaurant() {
        i++;
        counter.setText(String.format("%d/15", i + 1));
    }

    public void va_viewVoteNo(View view) {
//        setNoVote();
//                showVotes();
        if (placeIterator.hasNext()) {
            setRestaurantView(placeIterator.next());
            setNoVote(i);
            i++;
        } else {
//                    group.checkIfComplete();
            placeBuffer.release();
            finish();
        }
    }

    public void va_viewVoteYes(View view) {
//        setYesVote();
//                showVotes();
        if (placeIterator.hasNext()) {
            setRestaurantView(placeIterator.next());
            setNoVote(i);
            i++;
        } else {
//                    group.checkIfComplete();
            placeBuffer.release();
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ArrayList<String> list = new ArrayList<>(3);
        list.add("ChIJg-eOlowq9ocRrjQq2PKvNlc");
        list.add("ChIJHQRGthoq9ocRIzv4-kbWuQQ");
        list.add("ChIJ_RHaDDAq9ocRaC4eEpJ3gII");
        Places.GeoDataApi.getPlaceById(googleApiClient, list.toArray(new String[list.size()]))
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            placeBuffer = places;
                            placeIterator = places.iterator();
                            setRestaurantView(placeIterator.next());
                            for (Place place : places) {
                                log(ELog.i, "Place found: " + place.getName());
                            }
                        }
                        else {log(ELog.e, "Places not found");}
                    }
                });
    }

    private void setRestaurantView(Place place) {
        TextView textView = (TextView) findViewById(R.id.va_voting_restaurant_view);
        textView.setText(place.getName() + "\n" + place.getAddress() + "\n" + place.getPhoneNumber());
    }

    @Override
    public void onConnectionSuspended(int i) {
        log(ELog.w, "Google Places Api client connection was suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log(ELog.w, connectionResult.toString());
    }

//    private void showVotes() {
//        Toast.makeText(
//                VotingActivity.this,
//                voteContainer.getVotes().toString(),
//                Toast.LENGTH_LONG
//        ).show();
//    }

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

    private void setGoogleApiClient(BuildGoogleApiClientWorker buildGoogleApiClientAsyncTask) {
        try {
            googleApiClient = buildGoogleApiClientAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            log(ELog.e, e.getMessage(), e);
            return;
        }
        googleApiClient.connect();
    }
}
