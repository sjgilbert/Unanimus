package com.sjgilbert.unanimus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import javax.security.auth.login.LoginException;

/**
 * Activity for logging in.  Started from IntroPageActivity.
 */
public class LoginActivity extends UnanimusActivityTitle {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        setUnanimusTitle(R.string.login_activity_title);
        try {
            setTitleBar((ViewGroup) findViewById(R.id.login_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        usernameEditText = (EditText) findViewById(R.id.login_username);
        passwordEditText = (EditText) findViewById(R.id.login_password);

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                }
                catch (LoginException e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login() throws LoginException{
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isError = false;
        StringBuilder errorMessage = new StringBuilder();
        if (username.length() < minUserLen) {
            isError = true;
            errorMessage.append(String.format("Username must be at least %d characters in length", minUserLen));
        }
        if (password.length() < minPassLen) {
            if (isError) {
                errorMessage.append(", and ");
            }
            isError = true;
            errorMessage.append(String.format("Password must be at least %d characters in length", minPassLen));
        }
        errorMessage.append(".");

        if (isError) {
            throw new LoginException(errorMessage.toString());
        }

        final ProgressDialog wait = new ProgressDialog(LoginActivity.this);
        wait.setMessage(getString(R.string.wait_message));
        wait.show();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                wait.dismiss();
                if(e != null) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private EditText usernameEditText;
    private EditText passwordEditText;

    private final static int minUserLen = 4;
    private final static int minPassLen = 6;
}
