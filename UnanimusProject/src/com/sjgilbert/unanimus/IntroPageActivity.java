package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;

/**
 * The page that allows the user to either log-in or register_activity.
 */
public class IntroPageActivity extends UnanimusActivityTitle {
    static final int publicProfile = R.string.parse_public_profile;
    static final int userFriends = R.string.parse_user_friends;
    static final int appName = R.string.app_name;
    static final int facebookID = R.string.facebook_id_key;

    public IntroPageActivity() {
        super("ipa");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_page_activity);
        try {
            setTitleBar(R.string.ipa_title, (ViewGroup) findViewById(R.id.intro_page_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        Button facebookLogin = (Button) findViewById(R.id.ipa_facebook_login);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void facebookLogin() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(getString(publicProfile));
        permissions.add(getString(userFriends));

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (null != err) {
                    err.printStackTrace();
                    return;
                }
                if (user == null) {
                    Log.d(getString(appName), "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(getString(appName), "User signed up and logged in through Facebook!");
                    user.put(getString(facebookID), Profile.getCurrentProfile().getId()); //for future ParseUser queries
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            StartMainActivity();
                        }
                    });
                } else {
                    Log.d(getString(appName), "User logged in through Facebook!");
                    StartMainActivity();
                }
            }
        });
    }

    private void StartMainActivity() {
        Intent intent = new Intent(IntroPageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
