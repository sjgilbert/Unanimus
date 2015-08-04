package com.sjgilbert.unanimus;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;


/**
 * 8/2/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class PlacePickActivity extends UnanimusActivity {
    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.place_pick_activity);
        setTitle(R.string.place_pick_activity_title, findViewById(R.id.place_pick_activity));
    }

    public void startMap(View view) {
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
