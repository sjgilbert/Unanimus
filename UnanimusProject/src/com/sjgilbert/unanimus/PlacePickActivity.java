package com.sjgilbert.unanimus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle_TextEntryBar;


/**
 * 8/2/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class PlacePickActivity extends UnanimusActivityTitle_TextEntryBar {
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
    }

    public void startMap(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PLACE_PICKER_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                Place place = PlacePicker.getPlace(data, this);
                setPlacePreview(new String()
                        .concat(place.getName().toString() + "\n")
                        .concat(place.getAddress().toString() + "\n")
                        .concat(place.getPhoneNumber().toString() + "\n")
                        .concat("Rating: " + Float.toString(place.getRating())));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static final int PLACE_PICKER_REQUEST = 1;

    private boolean setPlacePreview(CharSequence fromPlace) {
        try {
            ((TextView) findViewById(R.id.place_pick_activity)
                    .findViewById(R.id.place_preview_layout)
                    .findViewById(R.id.place_as_string))
                    .setText(fromPlace);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
