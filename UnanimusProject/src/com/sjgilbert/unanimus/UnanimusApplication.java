package com.sjgilbert.unanimus;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class UnanimusApplication extends Application {
    public static final String UNANIMUS = "Unanimus";

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseObject.registerSubclass(UnanimusGroup.class);
        ParseObject.registerSubclass(VotingActivity.VaContainer.class);
        ParseObject.registerSubclass(UnanimusGroup2.class);
        ParseObject.registerSubclass(VoteContainer.class);

        Parse.initialize(
                this,
                "hHNXiaKrXkRDW4Ma50aVW3G5zma7NJyptGO795Nb",
                "0GO9X3HNPu9JMLGk6BH0yccRA1P143vQ6MWUvnpV"
        );

        ParseFacebookUtils.initialize(this);
    }
}
