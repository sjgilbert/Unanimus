package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * The activity which displays a specific group a user is a part of.  Should
 * eventually allow the user to indicate preferences/view recommendations.
 */
public class GroupActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("objID");
        }
        else {
            Toast.makeText(GroupActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        TextView groupNameTextView = (TextView) findViewById(R.id.group_name);
        groupNameTextView.setText("GROUP ID: " + groupName);

        ParseQuery<UnanimusGroup> query = ParseQuery.getQuery("UnanimusGroup");
        query.include("members");
        try{
            group = query.get(groupName);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        String creatorName = null;
        try {
            ParseUser creator = (ParseUser) group.get("user");
            creatorName = creator.fetchIfNeeded().getUsername();
        }
        catch(ParseException e) {
            System.out.println(e.getMessage());
        }

        TextView createdBy = (TextView) findViewById(R.id.group_created_by);
        createdBy.setText("Created by " + creatorName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.members, group.getMembers());
        ListView membersList = (ListView) findViewById(R.id.members_list);
        membersList.setAdapter(adapter);

    }
    private String groupName;
    private UnanimusGroup group;
}
