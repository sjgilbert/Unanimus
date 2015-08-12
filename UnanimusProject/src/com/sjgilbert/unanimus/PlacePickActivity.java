package com.sjgilbert.unanimus;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle_TextEntryBar;

import java.util.concurrent.ExecutionException;


/**
 * 8/2/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class PlacePickActivity
        extends UnanimusActivityTitle_TextEntryBar
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final static String PPA = "place_pick_activity";
    public final static String LAT_LNG = "ppa_place";

    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.place_pick_activity);
        try {
            setTitleBar(R.string.ppa_title, (ViewGroup) findViewById(R.id.place_pick_activity));
            setTextEntryBar(
                    R.string.ppa_address_hint,
                    R.string.ppa_address_button,
                    (ViewGroup) findViewById(R.id.place_pick_activity));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }

        new BuildGoogleApiClient().execute(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PLACE_PICKER_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setByPlace(PlacePicker.getPlace(data, this));
                updatePlacePreview();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            doSetResult();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (null != googleApiClient && null == lastLocation)
            refreshLastLocation(false);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(getString(R.string.app_name), connectionResult.toString());
    }

    public void setGoogleApiClient(BuildGoogleApiClient buildGoogleApiClient) {
        try {
            googleApiClient = buildGoogleApiClient.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        googleApiClient.connect();
    }

    public void viewFinish(View view) {
        finish();
    }

    public void viewSetByLastLocation(View view) {
        if (!setByLastLocation(lastLocation))
            updatePlacePreview();
    }

    public void viewRefreshLastLocation(View view) {
        refreshLastLocation(true);
        viewSetByLastLocation(view);
    }

    public void refreshLastLocation(boolean allowRecurse) {
        if (googleApiClient.isConnected()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else if (allowRecurse) {
            googleApiClient.reconnect();
        }
    }

    public void viewStartMap(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setByPlace(Place place) {
        latLng = place.getLatLng();
    }

    private boolean setByLastLocation(Location lastLocation) {
        if (null == lastLocation) {
            Toast.makeText(this, "Error retrieving last known location", Toast.LENGTH_LONG).show();
            return true;
        }
        latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        return false;
    }

    private String getPreviewString() {
        //noinspection RedundantStringConstructorCall
        return latLng.toString();
    }

    private boolean updatePlacePreview() {
        try {
            ((TextView) findViewById(R.id.place_pick_activity)
                    .findViewById(R.id.ppa_place_preview_layout)
                    .findViewById(R.id.ppa_place_as_string))
                    .setText(getPreviewString());
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private void doSetResult() {
        Intent returnIntent = new Intent();
        final int resultCode;
        if (null != latLng) {
            resultCode = RESULT_OK;

            Bundle bundle = new Bundle();
            bundle.putString(LAT_LNG, latLng.toString());

            returnIntent.putExtra(PPA, bundle);
        } else {
            resultCode = RESULT_CANCELED;
        }
        setResult(resultCode, returnIntent);
    }

    private static class BuildGoogleApiClient extends AsyncTask<PlacePickActivity, Object, GoogleApiClient> {
        private PlacePickActivity placePickActivity;

        protected static GoogleApiClient buildGoogleApiClient(
                Context context,
                GoogleApiClient.ConnectionCallbacks callbacks,
                GoogleApiClient.OnConnectionFailedListener connectionFailedListener
        ) {
            return new GoogleApiClient
                    .Builder(context)
                    .addConnectionCallbacks(callbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }

        @Override
        protected GoogleApiClient doInBackground(PlacePickActivity... params) {
            placePickActivity = params[0];
            return buildGoogleApiClient(placePickActivity, placePickActivity, placePickActivity);
        }

        @Override
        protected void onPostExecute(GoogleApiClient googleApiClient) {
            placePickActivity.setGoogleApiClient(this);
        }

    }

}
