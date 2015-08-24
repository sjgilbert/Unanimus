package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Profile;
import com.parse.ParseUser;

/**
 * This activity is the activity specified to begin on startup in AndroidManifest.xml.
 * It checks for a stored user, and if there isn't one, sends them to log-in/register_activity.
 */
public class StartupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();

        final String facebook_id_key = getResources().getString(R.string.facebook_id_key);

        if (currentUser == null) {
            startActivity(new Intent(this, IntroPageActivity.class));
            return;
        } else if (currentUser.get(facebook_id_key) == null) {
            try {
                currentUser.put(facebook_id_key, Profile.getCurrentProfile().getId()); //For future ParseUser queries
            } catch (NullPointerException e) {
                Log.e(getString(R.string.app_name), e.getMessage(), e);
                startActivity(new Intent(this, IntroPageActivity.class));
                return;
            }
        }

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
