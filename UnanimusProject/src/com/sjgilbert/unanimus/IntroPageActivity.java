package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
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
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("user_friends");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("Unanimus", "Uh oh. The user cancelled the Facebook login.");
                    return;
                } else if (user.isNew()) {
                    Log.d("Unanimus", "User signed up and logged in through Facebook!");
                    user.put("facebookID", Profile.getCurrentProfile().getId()); //for future ParseUser queries
                    user.saveInBackground();
                } else {
                    Log.d("Unanimus", "User logged in through Facebook!");
                }
                Intent intent = new Intent(IntroPageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
