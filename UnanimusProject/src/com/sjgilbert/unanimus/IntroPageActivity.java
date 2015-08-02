package com.sjgilbert.unanimus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    }
}
