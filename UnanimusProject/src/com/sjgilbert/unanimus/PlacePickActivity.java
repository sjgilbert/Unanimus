package com.sjgilbert.unanimus;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 8/2/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class PlacePickActivity
        extends UnanimusActivityTitle_TextEntryBar
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    /*
     *  Start static members
     */

    /*
     *  Start public static members
     */

    public final static String PPA = "ppa";
    public final static String LAT = "ppa_lat";
    public final static String LNG = "ppa_lng";

    /*
     *  End public static members
     */

    /*
     *  Start private static members
     */

    private final static int PLACE_PICKER_REQUEST = 1;

    /*
     *  End private static members
     */

    /*
     *  End static members
     */

    /*
     *  Start members
     */

    /*
     *  Start private members
     */

    private final BuildGoogleApiClientWorker googleApiClientWorker
            = new BuildGoogleApiClientWorker(this);
    private final BuildGeocoderAsyncTask geocoderWorker
            = new BuildGeocoderAsyncTask(this);

    private GoogleApiClient googleApiClient = null;
    private Geocoder geocoder = null;
    private Location lastLocation = null;
    private LatLng latLng = null;

    /*
     *  End private members
     */

    /*
     *  End members
     */

    /*
     *  Start Activity methods
     */

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

        EditText addressBar = getTextEntryEditText((ViewGroup) findViewById(R.id.place_pick_activity));
        addressBar.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

        getTextEntryButton((ViewGroup) findViewById(R.id.place_pick_activity))
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        ppa_viewGetByAddress(view);
                    }
                });

        googleApiClientWorker.execute();
        geocoderWorker.execute(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PLACE_PICKER_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setByPlace(PlacePicker.getPlace(data, this));
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

    /*
     *  End Activity methods
     */

    /*
     *  Start GoogleApiClient.ConnectionCallbacks methods
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        refreshLastLocation(false);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // do nothing
    }

    /*
     *  End GoogleApiClient.ConnectionCallbacks methods
     */

    /*
     *  Start GoogleApiClient.OnConnectionFailedListener methods
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(getString(R.string.app_name), connectionResult.toString());
    }

    /*
     *  End GoogleApiClient.OnConnectionFailedListener methods
     */

    /*
     *  Start AsyncTask setters
     */

    private void setGoogleApiClient(BuildGoogleApiClientWorker buildGoogleApiClientAsyncTask) {
        try {
            googleApiClient = buildGoogleApiClientAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        googleApiClient.connect();
    }

    private void setGeocoder(BuildGeocoderAsyncTask buildGeocoderAsyncTask) {
        try {
            geocoder = buildGeocoderAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /*
     *  End AsyncTask setters
     */

    /*
     *  Start onClick methods
     */

    public void ppa_viewFinish(View view) {
        finish();
    }

    public void ppa_viewSetByLastLocation(View view) {
        setByLastLocation(lastLocation);
    }

    public void ppa_viewRefreshLastLocation(View view) {
        refreshLastLocation(true);
        ppa_viewSetByLastLocation(view);
    }

    public void ppa_viewStartMap(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void ppa_viewGetByAddress(View view) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        int childCount = viewGroup.getChildCount();
        EditText addressBar = null;
        for (int i = 0; childCount > i; ++i) {
            View sibling = viewGroup.getChildAt(i);
            if (sibling instanceof EditText)
                addressBar = (EditText) sibling;
        }

        if (null == addressBar)
            return;

        String address = addressBar.getText().toString();

        setByString(address);
    }

    /*
     *  End onClick methods
     */

    /*
     *  Start set latLng by methods
     */

    private void setByLatLng(LatLng latLng) {
        this.latLng = latLng;
        updatePlacePreview();
    }

    private void setByPlace(Place place) {
        setByLatLng(place.getLatLng());
    }

    private void setByLastLocation(Location lastLocation) {
        if (null == lastLocation) {
            Toast.makeText(this, "Error retrieving last known location", Toast.LENGTH_LONG).show();
            return;
        }
        setByLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
    }

    private void refreshLastLocation(boolean reconnect) {
        if (AsyncTask.Status.FINISHED != googleApiClientWorker.getStatus())
            return;

        if (reconnect || ! googleApiClient.isConnected()) {
            googleApiClient.reconnect();
        } else {
            this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

    }

    private void setByString(String address) {
        if (AsyncTask.Status.FINISHED != geocoderWorker.getStatus()) {
            Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show();
            return;
        }
        SetByStringWorker.ParamsContainer paramsContainer
                = new SetByStringWorker.ParamsContainer(
                geocoder,
                address
        );

        new SetByStringWorker(this).execute(paramsContainer);
    }

    /*
     *  End set latLng by methods
     */

    /*
     *  Start latLng preview methods
     */

    private String getPreviewString() {
        return latLng.toString();
    }

    private void updatePlacePreview() {
        try {
            ((TextView) findViewById(R.id.place_pick_activity)
                    .findViewById(R.id.ppa_place_preview_layout)
                    .findViewById(R.id.ppa_place_as_string))
                    .setText(getPreviewString());
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    /* *  End latLng preview methods
     */


    /*
     *  Start result methods
     */

    private Bundle getResult() {
        Bundle bundle = new Bundle();
        bundle.putDouble(LAT, latLng.latitude);
        bundle.putDouble(LNG, latLng.longitude); return bundle;
    }

    private void doSetResult() {
        Intent returnIntent = new Intent();
        final int resultCode;
        if (null != latLng) {
            resultCode = RESULT_OK;
            returnIntent.putExtra(PPA, getResult());
        } else {
            resultCode = RESULT_CANCELED;
        }
        setResult(resultCode, returnIntent);
    }

    /*
     *  End result methods
     */

    /*
     * Start AsyncTask classes
     */

    private static abstract class PlacePickAsyncTask<T1, T2, T3>
            extends AsyncTask<T1, T2, T3>
    {
        private final PlacePickActivity placePickActivity;

        public PlacePickAsyncTask(PlacePickActivity placePickActivity) {
            super();
            this.placePickActivity = placePickActivity;
        }
    }

    private static class BuildGoogleApiClientWorker
            extends PlacePickAsyncTask<Object, Object, GoogleApiClient>
    {
        public BuildGoogleApiClientWorker(PlacePickActivity placePickActivity) {
            super(placePickActivity);
        }

        @Override
        protected GoogleApiClient doInBackground(Object... params) {
            return buildGoogleApiClient(
                    super.placePickActivity,
                    super.placePickActivity,
                    super.placePickActivity
            );
        }

        @Override
        protected void onPostExecute(GoogleApiClient googleApiClient) {
            super.placePickActivity.setGoogleApiClient(this);
        }

        private static GoogleApiClient buildGoogleApiClient(
                Context context,
                GoogleApiClient.ConnectionCallbacks callbacks,
                GoogleApiClient.OnConnectionFailedListener connectionFailedListener
        ) {
            return new GoogleApiClient
                    .Builder(context)
                    .addConnectionCallbacks(callbacks)
                    .addOnConnectionFailedListener(connectionFailedListener) .addApi(LocationServices.API) .build();
        }

    }

    private static class BuildGeocoderAsyncTask
            extends PlacePickAsyncTask<Object, Object, Geocoder>
    {
        public BuildGeocoderAsyncTask(PlacePickActivity placePickActivity) {
            super(placePickActivity);
        }

        @Override
        protected Geocoder doInBackground(Object... params) {
            return new Geocoder(super.placePickActivity);
        }

        @Override
        protected void onPostExecute(Geocoder geocoder) {
            super.placePickActivity.setGeocoder(this);
        }
    }

    private static class SetByStringWorker
        extends PlacePickAsyncTask<SetByStringWorker.ParamsContainer, Object, LatLng>
    {
        public SetByStringWorker(PlacePickActivity placePickActivity) {
            super(placePickActivity);
        }

        @Override protected LatLng doInBackground(ParamsContainer... params) { if (Status.FINISHED != super.placePickActivity.geocoderWorker.getStatus()) { new ExecutionException(new NullPointerException()).printStackTrace(); return null;
            }
            ParamsContainer paramsContainer = params[0];
            Geocoder geocoder = paramsContainer.geocoder;
            String address = paramsContainer.address;
            try {
                return getLatLngFromAddress(geocoder, address);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.placePickActivity.setByLatLng(latLng);
        }

        private static LatLng getLatLngFromAddress(
                Geocoder geocoder,
                String addressString
        )
                throws ExecutionException
        {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(addressString, 1);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
            LatLng latLng = null;
            try {
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return latLng;
        }

        public static class ParamsContainer {
            private final Geocoder geocoder;
            private final String address;

            public ParamsContainer(Geocoder geocoder, String address) {
                this.geocoder = geocoder;
                this.address = address;
            }
        }
    }

    /*
     *  End AsyncTask classes
     */
}
