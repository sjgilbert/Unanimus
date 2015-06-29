package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * View for joining a group.
 */
public class JoinGroupActivity extends Activity {

    private EditText groupID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group);

        groupID = (EditText) findViewById(R.id.join_group_id);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void joinGroup(View v) {
        ParseQuery query = ParseQuery.getQuery("group");
        Button submit = (Button) findViewById(R.id.submit_button);
        //submit.setText(query.getFirst().toString());
    }
}