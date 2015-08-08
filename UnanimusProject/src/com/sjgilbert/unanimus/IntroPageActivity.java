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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_page_activity);
        try {
            setTitleBar(R.string.intro_page_activity_title, (ViewGroup) findViewById(R.id.intro_page_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Button introRegister = (Button) findViewById(R.id.intro_page_activity_register_link);
        introRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPageActivity.this, RegisterActivity.class));
            }
        });

        Button login = (Button) findViewById(R.id.intro_page_activity_login_link);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPageActivity.this, LoginActivity.class));
            }
        });

        Button facebookLogin = (Button) findViewById(R.id.facebook_login);
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

    public void facebookLogin() {
        final String publicProfile = getResources().getString(R.string.parse_public_profile);
        final String userFriends = getResources().getString(R.string.parse_user_friends);
        final String appName = getResources().getString(R.string.app_name);
        final String facebookID = getResources().getString(R.string.facebook_id_key);

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(publicProfile);
        permissions.add(userFriends);

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (null != err) {
                    err.printStackTrace();
                    return;
                }
                if (user == null) {
                    Log.d(appName, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(appName, "User signed up and logged in through Facebook!");
                    user.put(facebookID, Profile.getCurrentProfile().getId()); //for future ParseUser queries
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            StartMainActivity();
                        }
                    });
                } else {
                    Log.d(appName, "User logged in through Facebook!");
                    StartMainActivity();
                }
            }
        });
    }

    protected void StartMainActivity() {
        Intent intent = new Intent(IntroPageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
