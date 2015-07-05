package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * This activity is the activity specified to begin on startup in AndroidManifest.xml.
 * It checks for a stored user, and if there isn't one, sends them to log-in/register.
 */
public class StartupActivity extends Activity {

    public StartupActivity() {
    }

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MakeGroupActivity.class));
        }
        else {
            startActivity(new Intent(this, IntroPageActivity.class));
        }
    }



}
