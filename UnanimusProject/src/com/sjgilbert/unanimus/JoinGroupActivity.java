package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle_TextEntryBar;

/**
 * View for joining a group_activity.
 */
public class JoinGroupActivity extends UnanimusActivityTitle_TextEntryBar {
    private EditText groupID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.join_group_activity);
        try {
            setTitleBar(R.string.jga_title, (ViewGroup) findViewById(R.id.join_group_activity));
            setTextEntryBar(
                    R.string.jga_group_id_hint,
                    R.string.jga_form_submission,
                    (ViewGroup) findViewById(R.id.join_group_activity));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }

        groupID = (EditText) findViewById(R.id.text_entry_bar).findViewById(R.id.text_field);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Button joinButton = (Button) findViewById(R.id.text_entry_bar).findViewById(R.id.submit_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupIDStr = groupID.getText().toString().trim();

                UnanimusGroup first;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("UnanimusGroup");
                try {
                    first = (UnanimusGroup) query.get(groupIDStr);
                    if (first.setMember(ParseUser.getCurrentUser())) {
                        first.save();
                        Toast.makeText(JoinGroupActivity.this, "Success!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(JoinGroupActivity.this, "Already a member of this group_activity!", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    Toast.makeText(JoinGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
