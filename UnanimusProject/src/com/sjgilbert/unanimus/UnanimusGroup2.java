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
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sam on 9/1/15.
 */
@ParseClassName("UnanimusGroup2")
public class UnanimusGroup2 extends ParseObject {
    private static final String VOTE_CONTAINERS = "voteContainerIds";
    private static final String USER_IDS = "userIds";
    private static final String RESTAURANT_IDS = "restaurantIds";

    private Map<String, VoteContainer> userIdsVc;
    private List<String> restaurantIds;

    private UnanimusGroup unanimusGroup;

    public UnanimusGroup2() throws ParseException {
        if (! has(CreateGroupActivity.CGA)
                || ! has(VOTE_CONTAINERS)
                || ! has(USER_IDS)
                || ! has(RESTAURANT_IDS))
            return;

        this.unanimusGroup = (UnanimusGroup) get(CreateGroupActivity.CGA);
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
            List<String> restaurantIds,
            UnanimusGroup unanimusGroup
    ) {
        this.userIdsVc = userIdsVc;
        this.restaurantIds = restaurantIds;
        this.unanimusGroup = unanimusGroup;

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

        add(CreateGroupActivity.CGA, unanimusGroup);
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
                    if (! unanimusGroup.isSet())
                        throw new IllegalStateException();

                    final ParseUser parseUser = ParseUser.getCurrentUser();

                    final int maxRestaurants = unanimusGroup.getMaxRestaurants();

                    final FpaContainer fpaContainer = unanimusGroup.getFpaContainer();
                    final PpaContainer ppaContainer = unanimusGroup.getPpaContainer();
                    final GspaContainer gspaContainer = unanimusGroup.getGspaContainer();

                    final FpaContainer.UserIdPair[] userIdPairs = fpaContainer.getUserIdPairs();

                    final LatLng latLng = ppaContainer.getLatLng();

                    final Date date = gspaContainer.getDate();
                    final int radius = gspaContainer.getRadius();
                    final GspaContainer.EPriceLevel ePriceLevel = gspaContainer.getPriceLevel();

                    final Map<String, VoteContainer> userIdsVc;
                    final List<String> restaurantIds;

                    userIdsVc = new Hashtable<>(userIdPairs.length);
                    restaurantIds = new ImmutableList<>(maxRestaurants);

                    for (int i = 0; restaurantIds.size() > i; ++i)
                        restaurantIds.set(i, BOOT_STRAP[i]);

                    for (FpaContainer.UserIdPair userIdPair : userIdPairs) {
                        final String voterId = userIdPair.parseUserId;
                        final List<String> readers = new LinkedList<>();

                        for (FpaContainer.UserIdPair idPair : userIdPairs) {
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

                        userIdsVc.put(voterId, voteContainer);
                    }

                    callback.done(new UnanimusGroup2(userIdsVc, restaurantIds, unanimusGroup));
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
