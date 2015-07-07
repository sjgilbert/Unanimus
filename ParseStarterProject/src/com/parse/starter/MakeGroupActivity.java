package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
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

import java.util.ArrayList;

/**
 * A button that creates a group.
 */
public class MakeGroupActivity extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_group);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());

		TextView groupIDTextView = (TextView) findViewById(R.id.echo_group_id);
		groupIDTextView.setVisibility(View.INVISIBLE);
	}

	public void makeGroup(View v) throws ParseException {
		final ProgressDialog wait = new ProgressDialog(MakeGroupActivity.this);
		wait.setMessage(getString(R.string.wait));
		wait.show();

		final UnanimusGroup newGroup = new UnanimusGroup();
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        newGroup.setACL(acl);
		newGroup.put("user",ParseUser.getCurrentUser());
        ArrayList<String> members = new ArrayList<String>();
        members.add(ParseUser.getCurrentUser().getUsername());
		newGroup.put("members", members);
		newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
				wait.dismiss();
                if (e == null) {
					Toast.makeText(MakeGroupActivity.this, "Success!", Toast.LENGTH_LONG).show();

                    displayGroupID(newGroup);
                } else {
                    Toast.makeText(MakeGroupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
