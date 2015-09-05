package com.sjgilbert.unanimus;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.FriendPickerActivity.FpaContainer;
import com.sjgilbert.unanimus.FriendPickerActivity.FpaContainer.UserIdPair;
import com.sjgilbert.unanimus.GroupSettingsPickerActivity.GspaContainer;
import com.sjgilbert.unanimus.GroupSettingsPickerActivity.GspaContainer.EPriceLevel;
import com.sjgilbert.unanimus.PlacePickActivity.PpaContainer;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("UnanimusGroup2")
public class UnanimusGroup2 extends ParseObject {
    private static final String VOTE_CONTAINER_IDS = "voteContainerIds";
    private static final String USER_IDS = "userIds";
    private static final String RESTAURANT_IDS = "restaurantIds";

    private final Map<String, VoteContainer> userIdsVc;
    private final List<String> restaurantIds;

    public UnanimusGroup2() throws ParseException {
        final List<String> voteIds = getList(VOTE_CONTAINER_IDS);
        final List<String> userIds = getList(USER_IDS);
        final List<String> parseRestaurantIds = getList(RESTAURANT_IDS);

        final int numUsers = userIds.size();

        if ((voteIds.size() != numUsers) || (userIds.size() != numUsers))
            throw new ParseException(ParseException.OTHER_CAUSE, "Received invalid data initializing UnanimusGroup2");

        userIdsVc = new Hashtable<>(numUsers);

        for (int i = 0; numUsers > i; ++i) {
            final String vId = voteIds.get(i);

            final ParseQuery parseQuery;
            if (ParseCache.parseCache.containsKey(vId)) {
                parseQuery = ParseCache.parseCache.get(vId);
            } else {
                parseQuery = ParseQuery.getQuery(VoteContainer.class);
                parseQuery.whereEqualTo("objectId", vId);
                ParseCache.parseCache.put(vId, (ParseQuery<ParseObject>) parseQuery);
            }

            VoteContainer voteContainer = (VoteContainer) parseQuery.getFirst();

            userIdsVc.put(userIds.get(i), voteContainer);
        }

        final int numRestaurants = parseRestaurantIds.size();

        this.restaurantIds = new ImmutableList<>(numRestaurants);

        for (int i = 0; numRestaurants > i; ++i) restaurantIds.set(i, parseRestaurantIds.get(i));
    }

    private UnanimusGroup2(
            Map<String, VoteContainer> userIdsVc,
            List<String> restaurantIds
    ) {
        this.userIdsVc = userIdsVc;
        this.restaurantIds = restaurantIds;

        addAll(VOTE_CONTAINER_IDS, userIdsVc.values());
        addAll(USER_IDS, userIdsVc.keySet());
        addAll(RESTAURANT_IDS, restaurantIds);
    }

    Map<String, VoteContainer> getUserIdsVc() {
        return userIdsVc;
    }

    List<String> getRestaurantIds() {
        return restaurantIds;
    }

    public static class Builder {
        private final UnanimusGroup unanimusGroup;

        public Builder(String parseCacheKey) throws ParseException {
            this.unanimusGroup =
                    (UnanimusGroup) ParseCache.parseCache.get(parseCacheKey).getFirst();
            if (! unanimusGroup.isSet())
                throw new IllegalArgumentException("UnanimusGroup is not set");
        }

        public void getInBackground(final Callback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ParseUser parseUser = ParseUser.getCurrentUser();

                    final int maxRestaurants = unanimusGroup.getMaxRestaurants();

                    final FpaContainer fpaContainer = unanimusGroup.getFpaContainer();
                    final PpaContainer ppaContainer = unanimusGroup.getPpaContainer();
                    final GspaContainer gspaContainer = unanimusGroup.getGspaContainer();

                    final UserIdPair[] userIdPairs = fpaContainer.getUserIdPairs();

                    final LatLng latLng = ppaContainer.getLatLng();

                    final Date date = gspaContainer.getDate();
                    final int radius = gspaContainer.getRadius();
                    final EPriceLevel ePriceLevel = gspaContainer.getPriceLevel();

                    final Map<String, VoteContainer> userIdsVc;
                    final List<String> restaurantIds;

                    userIdsVc = new Hashtable<>(maxRestaurants);
                    restaurantIds = new ImmutableList<>(maxRestaurants);

                    for (int i = 0; maxRestaurants > i; ++i) {
                        final String voterId = userIdPairs[i].parseUserId;
                        final List<String> readers = new LinkedList<>();

                        Iterator<String> iterator = readers.iterator();

                        while (iterator.hasNext())
                            if (iterator.next().contentEquals(voterId))
                                iterator.remove();

                        final VoteContainer voteContainer = new VoteContainer(
                                maxRestaurants,
                                parseUser,
                                voterId,
                                readers
                        );

                        voteContainer.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("Unanimus", e.getMessage(), e);
                                    return;
                                }

                                userIdsVc.put(voterId, voteContainer);
                            }
                        });

                        restaurantIds.set(i, BOOT_STRAP[i]);
                    }

                    callback.done(new UnanimusGroup2(userIdsVc, restaurantIds));
                }
            }).run();
        }

        private static final String[] BOOT_STRAP = new String[] {
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14"
        };

        interface Callback {
            void done(UnanimusGroup2 unanimusGroup2);
        }
    }
}
