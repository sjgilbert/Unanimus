package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity for registering for an account.  Started from IntroPageActivity.
 */
public class RegisterActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);

        usernameEditText = (EditText) findViewById(R.id.register_username);
        passwordEditText = (EditText) findViewById(R.id.register_password);
        repeatPasswordEditText = (EditText) findViewById(R.id.repeatpassword);

        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();

        if(username.length() != 0 && password.length() != 0 && password.equals(repeatPassword)) {
            final ProgressDialog wait = new ProgressDialog(RegisterActivity.this);
            wait.setMessage(getString(R.string.wait));
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
                        return;
                    }
                    else {
                        Intent intent = new Intent(RegisterActivity.this, MakeGroupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
        else {
            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
}
