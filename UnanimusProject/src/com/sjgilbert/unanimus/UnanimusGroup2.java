package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.FriendPickerActivity.FpaContainer;
import com.sjgilbert.unanimus.FriendPickerActivity.FpaContainer.UserIdPair;
import com.sjgilbert.unanimus.GroupSettingsPickerActivity.GspaContainer;
import com.sjgilbert.unanimus.GroupSettingsPickerActivity.GspaContainer.EPriceLevel;
import com.sjgilbert.unanimus.PlacePickActivity.PpaContainer;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("UnanimusGroup2")
public class UnanimusGroup2 extends ParseObject {
    private static final String VOTE_CONTAINERS = "voteContainerIds";
    private static final String USER_IDS = "userIds";
    private static final String RESTAURANT_IDS = "restaurantIds";

    private final Map<String, VoteContainer> userIdsVc;
    private final List<String> restaurantIds;

    public UnanimusGroup2() throws ParseException {
        final List<VoteContainer> voteIds = getList(VOTE_CONTAINERS);
        final List<String> userIds = getList(USER_IDS);
        final List<String> parseRestaurantIds = getList(RESTAURANT_IDS);

        final int numUsers = userIds.size();

        if ((voteIds.size() != numUsers) || (userIds.size() != numUsers))
            throw new ParseException(
                    ParseException.OTHER_CAUSE,
                    "Received invalid data while attempting to initialize UnanimusGroup2"
            );

        userIdsVc = new Hashtable<>(numUsers);

        for (int i = 0; numUsers > i; ++i) userIdsVc.put(userIds.get(i), voteIds.get(i));

        this.restaurantIds = new ImmutableList<>(parseRestaurantIds);
    }

    private UnanimusGroup2(
            Map<String, VoteContainer> userIdsVc,
            List<String> restaurantIds
    ) {
        this.userIdsVc = userIdsVc;
        this.restaurantIds = restaurantIds;

        commit();
    }

    Iterator<String> getRestaurantIterator() {
        return restaurantIds.iterator();
    }

    void vote(
            @NonNull String restaurantId,
            @NonNull final Vote vote,
            @Nullable final SaveCallback saveCallback
    ) {
        final VoteContainer voteContainer = userIdsVc.get(
                ParseUser.getCurrentUser().getObjectId()
        );

        final int index = restaurantIds.indexOf(restaurantId);
        vote.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                    if (saveCallback != null)
                        saveCallback.done(e);

                    return;
                }

                voteContainer.set(index, vote);
                voteContainer.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                            if (saveCallback != null)
                                saveCallback.done(e);

                            return;
                        }

                        Log.i(
                                UnanimusApplication.UNANIMUS,
                                String.format(
                                        Locale.getDefault(),
                                        "%s.  %s: %s",
                                        "Successfully saved vote container",
                                        ParseCache.OBJECT_ID,
                                        voteContainer.getObjectId()
                                )
                        );

                        if (saveCallback != null)
                            saveCallback.done(null);
                    }
                });
            }
        });
    }

    public Collection<String> getMembers() {
        return userIdsVc.keySet();
    }

    private void commit() {
        addAll(VOTE_CONTAINERS, userIdsVc.values());
        addAll(USER_IDS, userIdsVc.keySet());
        addAll(RESTAURANT_IDS, restaurantIds);
    }

    public static class Builder {
        private final UnanimusGroup unanimusGroup;

        public Builder(UnanimusGroup unanimusGroup) throws ParseException {
            this.unanimusGroup = unanimusGroup;
            if (! unanimusGroup.isSet())
                throw new IllegalArgumentException("UnanimusGroup is not set");
        }

        public UnanimusGroup2 getInBackground(final Callback callback) {
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

                    userIdsVc = new Hashtable<>(userIdPairs.length);
                    restaurantIds = new ImmutableList<>(maxRestaurants);

                    for (int i = 0; restaurantIds.size() > i; ++i)
                        restaurantIds.set(i, BOOT_STRAP[i]);

                    final AtomicInteger atomicInteger = new AtomicInteger(userIdPairs.length);

                    for (UserIdPair userIdPair : userIdPairs) {
                        final String voterId = userIdPair.parseUserId;
                        final List<String> readers = new LinkedList<>();

                        for (UserIdPair idPair : userIdPairs) {
                            final String id = idPair.parseUserId;
                            if (id.contentEquals(voterId)) continue;
                            readers.add(id);
                        }

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
                                    Log.e(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                                    return;
                                }

                                userIdsVc.put(voterId, voteContainer);

                                if (atomicInteger.decrementAndGet() == 0)
                                    callback.done(new UnanimusGroup2(userIdsVc, restaurantIds));
                            }
                        });
                    }
                }
            }).run();
            return null;
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
