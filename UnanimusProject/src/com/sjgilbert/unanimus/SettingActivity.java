package com.sjgilbert.unanimus;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by izzi on 7/7/15.
 */
public class SettingActivity extends UnanimusActivity {
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.setting_activity);
        setTitle(R.string.setting_activity_title, findViewById(R.id.setting_activity));
    }
}
