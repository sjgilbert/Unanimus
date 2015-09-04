package com.sjgilbert.unanimus;

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

    private VaContainer vaContainer;

    private UnanimusGroup group;
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
            groupKey = extras.getString("GROUP_ID");
        } else {
            Toast.makeText(VotingActivity.this, "NULL OBJ ID", Toast.LENGTH_LONG).show();
        }

        ParseQuery<ParseObject> parseQuery = ParseCache.parseCache.get(groupKey);

        if (parseQuery == null) {
            log(ELog.e, "Fucked up real bad");
            finish();
        }

        assert parseQuery != null;

        try {
            group = (UnanimusGroup) parseQuery.getFirst();
        } catch (ClassCastException | ParseException e) {
            log(ELog.e, e.getMessage(), e);
            finish();
        }

        ParseQuery<UnanimusGroup> query = UnanimusGroup.getQuery();
        try {
            group = query.get(groupKey);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        vaContainer = new VaContainer();

        counter = (TextView) findViewById(R.id.va_voting_counter);
        restaurants = group.getRestaurants();
        System.out.println(restaurants.size());

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
                    group.addVoteArray(vaContainer.getVotes());
                    group.checkIfComplete();
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
                    group.addVoteArray(vaContainer.getVotes());
                    group.checkIfComplete();
                    returnIntentFinish();
                    showVotes();
                }
            }
        });

        ParseQuery.clearAllCachedResults();
    }

    @Override
    protected void onStop() {
        ParseQuery.clearAllCachedResults();

        super.onStop();
    }

    @Override
    public void finish() {
        if (group == null) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK);

        super.finish();
    }

    private void setYesVote() {
        vaContainer.votes.add(YES);
    }

    private void setNoVote() {
        vaContainer.votes.add(NO);
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

    @Deprecated
    private void returnIntentFinish() {
        finish();
    }

    @ParseClassName("VaContainer")
    public static class VaContainer extends ParseObject {
        public final static String VOTES = "votes";

        private final ArrayList<Integer> votes;

        public VaContainer() {
            votes = new ArrayList<>();
        }

        VaContainer(Bundle bundle) {
            this.votes = bundle.getIntegerArrayList(VOTES);
        }

        Bundle getAsBundle() {
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(VOTES, votes);

            return bundle;
        }

        public ArrayList<Integer> getVotes() {
            return votes;
        }
    }
}
