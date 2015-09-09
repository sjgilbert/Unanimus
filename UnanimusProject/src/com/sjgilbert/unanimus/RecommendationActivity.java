package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.concurrent.ExecutionException;

/**
 * Created by sam on 8/23/15.
 */
public class RecommendationActivity
        extends UnanimusActivityTitle
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String RECA = "reca";

    private String groupName;
    private CgaContainer group;
    private GoogleApiClient googleApiClient = null;
    private final BuildGoogleApiClientWorker googleApiClientWorker
            = new BuildGoogleApiClientWorker(this);


    public RecommendationActivity() {
        super(RECA);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recommendation_activity);
        try {
            setTitleBar(R.string.reca_title, (ViewGroup) findViewById(R.id.recommendation_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();    //The GROUP_ID of the selected group_activity
        if (extras != null) {
            groupName = extras.getString(GroupActivity.GROUP_ID);
        } else {
            Toast.makeText(RecommendationActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        ParseQuery<CgaContainer> query = CgaContainer.getQuery();
        query.include("members");
        query.include("user");
        try {
            group = query.get(groupName);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Places.GeoDataApi.getPlaceById(googleApiClient, "ChIJh2E4tQIq9ocRmxkXDVB0zZQ")
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            TextView recommendation = (TextView) findViewById(R.id.reca_recommendation);
                            recommendation.setText(places.get(0).getName() + "\n" + places.get(0).getAddress().toString().split(",")[0] + "\n" + places.get(0).getPhoneNumber().toString().split(" ")[1]);
                            log(ELog.i, "Place found: " + places.get(0).getName());
                        } else log(ELog.e, "Places not found");
                    }
                });
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
        private final RecommendationActivity recommendationActivity;

        public VotingActivityAsyncTask(RecommendationActivity recommendationActivity) {
            super();
            this.recommendationActivity = recommendationActivity;
        }
    }

    private static class BuildGoogleApiClientWorker
            extends VotingActivityAsyncTask<Object, Object, GoogleApiClient> {
        public BuildGoogleApiClientWorker(RecommendationActivity recommendationActivity) {
            super(recommendationActivity);
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
                    super.recommendationActivity,
                    super.recommendationActivity,
                    super.recommendationActivity
            );
        }

        @Override
        protected void onPostExecute(GoogleApiClient googleApiClient) {
            super.recommendationActivity.setGoogleApiClient(this);
        }

    }
}
