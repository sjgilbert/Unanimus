package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The page that allows the user to either log-in or register.
 */
public class IntroPageActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_page);

        Button introRegister = (Button) findViewById(R.id.intro_register);
        introRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPageActivity.this, RegisterActivity.class));
            }
        });

        Button login = (Button) findViewById(R.id.intro_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                startActivity(new Intent(IntroPageActivity.this, LoginActivity.class));
            }
        });
    }
}
