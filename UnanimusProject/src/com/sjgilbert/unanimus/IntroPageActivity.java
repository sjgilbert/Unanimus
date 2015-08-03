package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The page that allows the user to either log-in or register_activity.
 */
public class IntroPageActivity extends UnanimusActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_page_activity);
        setTitle(R.string.intro_page_activity_title, findViewById(R.id.intro_page_activity));

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
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("user_friends");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    return;
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                }
                Intent intent = new Intent(IntroPageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}

//AccessToken accessToken = AccessToken.getCurrentAccessToken();
//GraphRequest request = GraphRequest.newMeRequest(
//        accessToken,
//        new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(
//                    JSONObject object,
//                    GraphResponse response) {
//            }
//        });
//Bundle parameters = new Bundle();
//parameters.putString("fields", "id,name,link");
//        request.setParameters(parameters);
//        request.executeAsync();
