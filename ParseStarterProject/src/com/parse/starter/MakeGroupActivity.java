package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;


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
		TextView groupID = (TextView) findViewById(R.id.group_id);
		groupID.setVisibility(View.INVISIBLE);

		final ParseObject newGroup = new ParseObject("NewGroup");
		newGroup.put("user", "testUser");
		newGroup.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					Button makeGroupButton1 = (Button) findViewById(R.id.make_group);
					makeGroupButton1.setText("Group Made!");

					displayGroupID(newGroup);
				} else {
					Button makeGroupButton1 = (Button) findViewById(R.id.make_group);
					makeGroupButton1.setEnabled(true);
					makeGroupButton1.setText("Make Group");

					displayError();
				}
			}
		});
	}

	private void displayError() {
		String text = "ERROR: GROUP NOT CREATED";
		TextView idTextView = (TextView) findViewById(R.id.group_id);
		idTextView.setText(text);
		idTextView.setVisibility(View.VISIBLE);
	}

	private void displayGroupID(ParseObject group) {
		String groupID = group.getObjectId();
		String text = "Group ID: " + groupID;
		TextView idTextView = (TextView) findViewById(R.id.group_id);
		idTextView.setText(text);
		idTextView.setVisibility(View.VISIBLE);
	}
}
