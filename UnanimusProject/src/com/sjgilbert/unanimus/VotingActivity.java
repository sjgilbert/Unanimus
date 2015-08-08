package com.sjgilbert.unanimus;

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
    private int i;
    private TextView counter;
    private List<String> restaurants;
    private ArrayList<Integer> votes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voting_activity);
        try {
            setTitleBar(R.string.voting_activity_title, (ViewGroup) findViewById(R.id.voting_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        counter = (TextView) findViewById(R.id.voting_counter);
        restaurants = new ArrayList<>(NUMBER_OF_RESTAURANTS);
        for(int i = 1; i <= NUMBER_OF_RESTAURANTS; i++) {
            restaurants.add(String.format("Restaurant %d", i));
        }
        votes = new ArrayList<>();
        for(int i = 1; i <= NUMBER_OF_RESTAURANTS; i++) {
            votes.add(0);
        }

        final TextView restaurant = (TextView) findViewById(R.id.voting_restaurant_view);
        restaurant.setText(restaurants.get(i));

        Button yesButton = (Button) findViewById(R.id.voting_yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    votes.set(i, YES);
                    i++;
                    counter.setText(String.format("%d/15", i + 1));
                    restaurant.setText(restaurants.get(i));
                } else {
                    votes.set(i, YES);
                    Toast.makeText(VotingActivity.this, "DONE", Toast.LENGTH_LONG).show();
                    System.out.println(votes.toString());
                }
            }
        });

        Button noButton = (Button) findViewById(R.id.voting_no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < NUMBER_OF_RESTAURANTS - 1) {
                    votes.set(i, NO);
                    i++;
                    counter.setText(String.format("%d/15", i + 1));
                    restaurant.setText(restaurants.get(i));
                }
                else {
                    votes.set(i, NO);
                    Toast.makeText(VotingActivity.this, "DONE", Toast.LENGTH_LONG).show();
                    System.out.println(votes.toString());
                }
            }
        });
    }

    private static final int NUMBER_OF_RESTAURANTS = 15;
    private static final int YES = 1;
    private static final int NO = -1;
}
