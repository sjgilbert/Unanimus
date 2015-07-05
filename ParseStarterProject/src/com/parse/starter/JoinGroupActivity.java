package com.parse.starter;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

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
        String groupIDStr = groupID.getText().toString().trim();

        UnanimusGroup first;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UnanimusGroup");
        try {
            first = (UnanimusGroup) query.get(groupIDStr);
            first.setMember(ParseUser.getCurrentUser());
            first.save();
        }
        catch(ParseException e) {
            Toast toast = Toast.makeText(JoinGroupActivity.this,e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Toast toast = Toast.makeText(JoinGroupActivity.this,first.getObjectId(),Toast.LENGTH_LONG);
        toast.show();

    }
}
