package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Profile;
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
            if(currentUser.get("facebookID")==null) {
                currentUser.put("facebookID", Profile.getCurrentProfile().getId()); //For future ParseUser queries
            }
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, IntroPageActivity.class));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
