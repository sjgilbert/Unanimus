package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

/**
 * Created by sam on 8/23/15.
 */
public class RecommendationActivity extends UnanimusActivityTitle {
    private String groupName;
    private CgaContainer group;

    public RecommendationActivity() {
        super("reca");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recommendation_activity);
        try {
            setTitleBar(R.string.reca_title, (ViewGroup) findViewById(R.id.recommendation_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();    //The GROUP_ID of the selected group_activity
        if (extras != null) {
            groupName = extras.getString("GROUP_ID");
        } else {
            Toast.makeText(RecommendationActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        ParseQuery<CgaContainer> query = CgaContainer.getQuery();
        query.include("members");
        query.include("user");
        try {
            group = query.get(groupName);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        TextView recommendation = (TextView) findViewById(R.id.reca_recommendation);
        recommendation.setText((String) group.get("recommendation"));
    }
}
