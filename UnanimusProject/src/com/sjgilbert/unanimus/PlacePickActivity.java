package com.sjgilbert.unanimus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle_TextEntryBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * 8/2/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class PlacePickActivity
        extends
        UnanimusActivityTitle_TextEntryBar
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {
    final static String PPA = "ppaContainer";

    private final static String TAG = "ppa";
    private final static int PLACE_PICKER_REQUEST = 1;

    private final BuildGoogleApiClientWorker googleApiClientWorker
            = new BuildGoogleApiClientWorker(this);
    private final BuildGeocoderAsyncTask geocoderWorker
            = new BuildGeocoderAsyncTask(this);
    private final OnCreateWorker createWorker = new OnCreateWorker();
    private final PpaContainer ppaContainer = new PpaContainer();
    private GoogleApiClient googleApiClient = null;
    private Geocoder geocoder = null;
    private Location lastLocation = null;
    private MapLocationSource locationSource;

    public PlacePickActivity() {
        super(TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.place_pick_activity);

        createWorker.execute();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.locationSource = new MapLocationSource(map);
        map.setLocationSource(locationSource);

        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setIndoorEnabled(false);
        map.setTrafficEnabled(true);

        final UiSettings uiSettings = map.getUiSettings();

        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);

        attemptSetDefault();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.finish();
    }
    
    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        final int resultCode;
        if (ppaContainer.isSet()) {
            try {
                returnIntent.putExtra(PPA, ppaContainer.getAsBundle());
            } catch (IContainer.NotSetException e) {
                log(ELog.e, e.getMessage(), e);
            }
            resultCode = RESULT_OK;
        } else {
            resultCode = RESULT_CANCELED;
        }
        setResult(resultCode, returnIntent);

        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PLACE_PICKER_REQUEST == requestCode) {
            if (RESULT_OK == resultCode)
                setByPlace(PlacePicker.getPlace(data, this));
        } else throw new IllegalArgumentException();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        refreshLastLocation(false);

        attemptSetDefault();
    }

    @Override
    public void onConnectionSuspended(int i) {
        log(ELog.w, "Google Places Api client connection was suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log(ELog.w, connectionResult.toString());
    }

    private void attemptSetDefault() {
        if (!ppaContainer.isSet()
                && (locationSource != null)
                && (lastLocation != null))
            setByLastLocation(lastLocation);
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

    private void setGeocoder(BuildGeocoderAsyncTask buildGeocoderAsyncTask) {
        try {
            geocoder = buildGeocoderAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            log(ELog.e, e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
    public void ppa_viewFinish(@SuppressWarnings("UnusedParameters") View view) {
        if (ppaContainer.getLatLng() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlacePickActivity.this);
            builder.setMessage("No location selected!  Continue anyway?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else finish();
    }

    @SuppressWarnings("WeakerAccess")
    public void ppa_viewSetByLastLocation(@SuppressWarnings("UnusedParameters") View view) {
        setByLastLocation(lastLocation);
    }

    @SuppressWarnings("unused")
    public void ppa_viewRefreshLastLocation(View view) {
        refreshLastLocation(true);
        ppa_viewSetByLastLocation(view);
    }

    @SuppressWarnings("unused")
    public void ppa_viewStartMap(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            log(ELog.e, e.getMessage(), e);
        }
    }

    @SuppressWarnings("WeakerAccess")
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

    private void setByLatLng(LatLng latLng) {
        this.ppaContainer.setFromLatLng(latLng);
        updatePlacePreview(latLng);
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
        if (AsyncTask.Status.FINISHED != googleApiClientWorker.getStatus()) {
            Log.w(getString(R.string.app_name), "Attempt to refresh location, but Google API client is still building.");
            return;
        }

        if (reconnect || !googleApiClient.isConnected()) {
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

    private String getPreviewString(LatLng latLng) {
        return latLng.toString();
    }

    private void updatePlacePreview(LatLng latLng) {
        log(
                ELog.i,
                String.format(
                        Locale.getDefault(),
                        "%s: %s",
                        "Selected location: ",
                        getPreviewString(latLng)
                )
        );
        locationSource.update(latLng);
    }

    private static class MapLocationSource implements LocationSource {
        final GoogleMap googleMap;
        OnLocationChangedListener locationChangedListener;
        Marker marker;

        MapLocationSource(GoogleMap map) {
            this.googleMap = map;
        }

        void update(LatLng latLng) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));

            if (null != marker) marker.remove();
            marker = googleMap.addMarker(new MarkerOptions().position(latLng));
        }

        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            this.locationChangedListener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {
            locationChangedListener = null;
        }
    }

    private static abstract class PlacePickAsyncTask<T1, T2, T3>
            extends AsyncTask<T1, T2, T3> {
        private final PlacePickActivity placePickActivity;

        public PlacePickAsyncTask(PlacePickActivity placePickActivity) {
            super();
            this.placePickActivity = placePickActivity;
        }
    }

    private static class BuildGoogleApiClientWorker
            extends PlacePickAsyncTask<Object, Object, GoogleApiClient> {
        public BuildGoogleApiClientWorker(PlacePickActivity placePickActivity) {
            super(placePickActivity);
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
                    .addApi(LocationServices.API)
                    .build();
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

    }

    private static class BuildGeocoderAsyncTask
            extends PlacePickAsyncTask<Object, Object, Geocoder> {
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
            extends PlacePickAsyncTask<SetByStringWorker.ParamsContainer, Object, LatLng> {
        public SetByStringWorker(PlacePickActivity placePickActivity) {
            super(placePickActivity);
        }

        private static LatLng getLatLngFromAddress(
                Geocoder geocoder,
                String addressString
        )
                throws ExecutionException {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(addressString, 1);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        }

        @Override
        protected LatLng doInBackground(ParamsContainer... params) {
            if (Status.FINISHED != super.placePickActivity.geocoderWorker.getStatus()) {
                Log.w(
                        "Unanimus",
                        "Failed to query address because the geocoder builder is not finished"
                );
                return null;
            }

            ParamsContainer paramsContainer = params[0];
            Geocoder geocoder = paramsContainer.geocoder;
            String address = paramsContainer.address;
            try {
                return getLatLngFromAddress(geocoder, address);
            } catch (ExecutionException | NullPointerException e) {
                Log.e(
                        "Unanimus",
                        e.getMessage(),
                        e
                );
                return null;
            }
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (null == latLng) {
                super.placePickActivity.log(ELog.d, "Address query gave zero results");
                return;
            }
            super.placePickActivity.setByLatLng(latLng);
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

    private class OnCreateWorker extends AsyncTask<Void, Void, OnCreateWorker.Container> {
        @Override
        protected Container doInBackground(Void... params) {
            final ViewGroup vg = (ViewGroup) findViewById(R.id.place_pick_activity);
            final Button bt = getTextEntryButton(vg);
            final EditText et = getTextEntryEditText(vg);
            final MapFragment mf = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.ppa_map);

            return new Container(vg, bt, et, mf);
        }

        @Override
        protected void onPostExecute(OnCreateWorker.Container result) {
            result.bt.setOnClickListener(new btClickListener());

            result.et.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

            result.mf.getMapAsync(PlacePickActivity.this);

            setTitleBar(R.string.ppa_title, result.vg);
            setTextEntryBar(R.string.ppa_address_hint, R.string.ppa_address_button, result.vg);

            googleApiClientWorker.execute();
            geocoderWorker.execute(this);
        }

        class Container {
            final ViewGroup vg;
            final Button bt;
            final EditText et;
            final MapFragment mf;

            Container(ViewGroup vg, Button bt, EditText et, MapFragment mf) {
                this.vg = vg;
                this.bt = bt;
                this.et = et;
                this.mf = mf;
            }
        }

        class btClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ppa_viewGetByAddress(v);
            }
        }
    }
}
