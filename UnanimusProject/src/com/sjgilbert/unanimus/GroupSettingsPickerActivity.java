package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

/**
 * Created by sam on 8/9/15.
 */
public class GroupSettingsPickerActivity extends UnanimusActivityTitle{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_settings_picker_activity);
        try {
//            setTitleBar(R.string._title, (ViewGroup) findViewById(R.id.group_settings_picker_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
