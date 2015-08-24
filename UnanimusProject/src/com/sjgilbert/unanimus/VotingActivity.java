package com.sjgilbert.unanimus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity for voting on restaurants
 */
public class VotingActivity extends UnanimusActivityTitle {
    private static final int NUMBER_OF_RESTAURANTS = 15;
    private static final int YES = 1;
    private static final int NO = -1;

    private VaContainer vaContainer;

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

        vaContainer = new VaContainer();

        counter = (TextView) findViewById(R.id.va_voting_counter);
        restaurants = new ArrayList<>(NUMBER_OF_RESTAURANTS);
        for (int i = 1; i <= NUMBER_OF_RESTAURANTS; i++) {
            restaurants.add(String.format("Restaurant %d", i));
        }

        final TextView restaurant = (TextView) findViewById(R.id.va_voting_restaurant_view);
        restaurant.setText(restaurants.get(i));

        Button yesButton = (Button) findViewById(R.id.va_voting_yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    setYesVote();
                    incrementRestaurant();
                    showVotes();
                } else {
                    setYesVote();
                    returnIntentFinish();
                    showVotes();
                }
            }
        });

        Button noButton = (Button) findViewById(R.id.va_voting_no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    setNoVote();
                    incrementRestaurant();
                    showVotes();
                } else {
                    setNoVote();
                    returnIntentFinish();
                    showVotes();
                }
            }
        });
    }

    private void setYesVote() {
        vaContainer.votes.set(i, YES);
    }

    private void setNoVote() {
        vaContainer.votes.set(i, NO);
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
                vaContainer.getVotes().toString(),
                Toast.LENGTH_LONG
        ).show();
    }

    private void returnIntentFinish() {
        Intent intent = new Intent();
        intent.putExtra("vaContainer", vaContainer.getAsBundle());
        setResult(RESULT_OK);
        finish();
    }

    static class VaContainer {
        final static String VOTES = "votes";

        private final ArrayList<Integer> votes;

        VaContainer() {
            votes = new ArrayList<>(NUMBER_OF_RESTAURANTS);

            for (int i = 1; i <= NUMBER_OF_RESTAURANTS; i++) {
                votes.add(0);
            }
        }

        @SuppressWarnings("unused")
        VaContainer(Bundle bundle) {
            this.votes = bundle.getIntegerArrayList(VOTES);
        }

        Bundle getAsBundle() {
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(VOTES, votes);

            return bundle;
        }

        ArrayList<Integer> getVotes() {
            return votes;
        }
    }
}
