package com.sjgilbert.unanimus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import javax.security.auth.login.LoginException;

/**
 * Activity for registering for an account.  Started from IntroPageActivity.
 */
public class RegisterActivity extends UnanimusActivityTitle {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_activity);
        setUnanimusTitle(R.string.register_activity_title);
        try {
            setTitleBar((ViewGroup) findViewById(R.id.register_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        usernameEditText = (EditText) findViewById(R.id.register_username);
        passwordEditText = (EditText) findViewById(R.id.register_password);
        repeatPasswordEditText = (EditText) findViewById(R.id.repeatpassword);

        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    register();
                }
                catch (LoginException e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register() throws LoginException{
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

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
        if ( !password.equals(repeatPassword) ) {
            if (isError) {
                errorMessage.append(", and ");
            }
            isError = true;
            errorMessage.append("Passwords must match");
        }
        errorMessage.append(".");

        if (isError) {
            throw new LoginException(errorMessage.toString());
        }

        final ProgressDialog wait = new ProgressDialog(RegisterActivity.this);
        wait.setMessage(getString(R.string.wait_message));
        wait.show();

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                wait.dismiss();
                if (e != null) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    // Requires API 11
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;

    private static final int minUserLen = 4;
    private static final int minPassLen = 6;
}
