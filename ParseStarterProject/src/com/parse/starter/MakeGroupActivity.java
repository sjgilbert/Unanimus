package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A button that creates a group.
 */
public class MakeGroupActivity extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
	}

	public void makeGroup(View v) throws ParseException {
		Button makeGroupButton = (Button) findViewById(R.id.make_group);
		makeGroupButton.setEnabled(false);
		makeGroupButton.setText("Creating ...");
		TextView groupID = (TextView) findViewById(R.id.echo_group_id);
		groupID.setVisibility(View.INVISIBLE);

		final UnanimusGroup newGroup = new UnanimusGroup();
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        newGroup.setACL(acl);
        ArrayList<String> members = new ArrayList<String>();
        members.add(ParseUser.getCurrentUser().toString());
		newGroup.put("members", members);
		newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Button makeGroupButton = (Button) findViewById(R.id.make_group);
                    makeGroupButton.setText("Group Made!");

                    displayGroupID(newGroup);
                    Toast toast = Toast.makeText(MakeGroupActivity.this, newGroup.getMember(0).toString(), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Button makeGroupButton1 = (Button) findViewById(R.id.make_group);
                    makeGroupButton1.setEnabled(true);
                    makeGroupButton1.setText("Make Group");

                    Toast toast = Toast.makeText(MakeGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
	}

	private void displayGroupID(ParseObject group) {
		String groupID = group.getObjectId();
		String text = "Group ID: " + groupID;
		TextView idTextView = (TextView) findViewById(R.id.echo_group_id);
		idTextView.setText(text);
		idTextView.setVisibility(View.VISIBLE);
	}
}
