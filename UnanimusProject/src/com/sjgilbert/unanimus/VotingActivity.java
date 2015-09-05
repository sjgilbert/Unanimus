package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sjgilbert.unanimus.parsecache.ParseCache;
import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity for voting on restaurants
 */
public class VotingActivity extends UnanimusActivityTitle {
    public static final int NUMBER_OF_RESTAURANTS = 15;
    private static final int YES = 1;
    private static final int NO = -1;

    private VoteContainer voteContainer;

    private UnanimusGroup2 group;
    private String groupKey;

    private int i;
    private TextView counter;
    private List<String> restaurants;

    public VotingActivity() {
        super("va");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voting_activity);
        try {
            setTitleBar(R.string.voting_activity_title, (ViewGroup) findViewById(R.id.voting_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        Bundle extras = getIntent().getExtras();    //The GROUP_ID of the selected group_activity
        if (extras != null) {
            groupKey = extras.getString(GroupActivity.GROUP_ID);
        } else {
            Toast.makeText(VotingActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        ParseQuery<ParseObject> query = ParseCache.parseCache.get(groupKey);
        if (query == null) {
            log(ELog.e, "messed up");
            finish();
        }

        assert query != null;

        try {
            group = (UnanimusGroup2) query.getFirst();
        } catch (ClassCastException | ParseException e) {
            log(ELog.e, e.getMessage(), e);
            finish();
        }

        ParseQuery<VoteContainer> voteQuery = VoteContainer.getQuery();

        try {
            voteContainer = voteQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        counter = (TextView) findViewById(R.id.va_voting_counter);
        restaurants = group.getRestaurantsIds();

        final TextView restaurant = (TextView) findViewById(R.id.va_voting_restaurant_view);
        restaurant.setText(restaurants.get(i));

        Button yesButton = (Button) findViewById(R.id.va_voting_yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setYesVote();
                showVotes();
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    incrementRestaurant();
                } else {
                    voteContainer.setVotes(voteContainer.getAl());
                    voteContainer.saveInBackground();
//                    group.checkIfComplete();
                    finish();
                }
            }
        });

        Button noButton = (Button) findViewById(R.id.va_voting_no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoVote();
                showVotes();
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    incrementRestaurant();
                } else {
                    voteContainer.setVotes(voteContainer.getAl());
                    voteContainer.saveInBackground();
//                    group.checkIfComplete();
                    finish();
                }
            }
        });

        ParseQuery.clearAllCachedResults();
    }

    private void setYesVote() {
        voteContainer.add(YES);
    }

    private void setNoVote() {voteContainer.add(NO);
    }

    private void incrementRestaurant() {
        i++;
        counter.setText(String.format("%d/15", i + 1));

        TextView restaurant = (TextView) findViewById(R.id.va_voting_restaurant_view);
        restaurant.setText(restaurants.get(i));
    }

    private void showVotes() {
        Toast.makeText(
                VotingActivity.this,
                voteContainer.getVotes().toString(),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onStop() {
        ParseQuery.clearAllCachedResults();
        super.onStop();
    }

    @Override
    public void finish() {
        if (group == null) {
            setResult(RESULT_CANCELED);
        }
        else {
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
