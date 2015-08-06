package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * This activity is the activity specified to begin on startup in AndroidManifest.xml.
 * It checks for a stored user, and if there isn't one, sends them to log-in/register_activity.
 */
public class StartupActivity extends Activity {

    public StartupActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser;
        try {
            currentUser = ParseUser.getCurrentUser();
        } catch (NullPointerException e) {
            e.printStackTrace();
            currentUser = null;
        }

        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, IntroPageActivity.class));
        }
    }
}
