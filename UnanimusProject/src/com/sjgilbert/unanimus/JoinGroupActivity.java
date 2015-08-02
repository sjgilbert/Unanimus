package com.sjgilbert.unanimus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * View for joining a group_activity.
 */
public class JoinGroupActivity extends UnanimusActivity {

    private EditText groupID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.join_group_activity);
        setTitle(R.string.join_group_activity_title, findViewById(R.id.join_group_activity));

        groupID = (EditText) findViewById(R.id.join_group_id_field);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Button joinButton = (Button) findViewById(R.id.join_group_form_submission);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupIDStr = groupID.getText().toString().trim();

                UnanimusGroup first;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("UnanimusGroup");
                try {
                    first = (UnanimusGroup) query.get(groupIDStr);
                    if(first.setMember(ParseUser.getCurrentUser())) {
                        first.save();
                        Toast.makeText(JoinGroupActivity.this,"Success!",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(JoinGroupActivity.this,"Already a member of this group_activity!", Toast.LENGTH_LONG);
                    }
                }
                catch(ParseException e) {
                    Toast.makeText(JoinGroupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
