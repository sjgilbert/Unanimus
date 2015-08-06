package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
            setTitleBar(R.string.place_pick_activity_title, (ViewGroup) findViewById(R.id.place_pick_activity));
            setTextEntryBar(
                    R.string.place_pick_activity_address_hint,
                    R.string.place_pick_activity_address_button,
                    (ViewGroup) findViewById(R.id.place_pick_activity));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void startMap(View view) {
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
