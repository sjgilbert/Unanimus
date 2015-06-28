package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.parse.ParseAnalytics;

/**
 * Created by sam on 6/28/15.
 */
public class JoinGroupActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void joinGroup(View v) {

    }
}