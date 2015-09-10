package com.sjgilbert.unanimus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by sam on 9/1/15.
 */
@SuppressWarnings("WeakerAccess")
@ParseClassName("UnanimusGroup")
public class UnanimusGroup extends ParseObject {
    private static final String VOTE_CONTAINERS = "voteContainerIds";
    private static final String USER_IDS = "userIds";
    public static final String RESTAURANT_IDS = "restaurantIds";
    private static final String RECOMMENDATION = "recommendation";

    public List<String> getRecommendation() {
        return recommendation;
    }

    @NonNull private List<String> recommendation = new ArrayList<>();

    private Map<String, VotesList> userIdsVc;

    List<String> getRestaurantIds() {
        return restaurantIds;
    }

    private List<String> restaurantIds;

    static ParseQuery getQuery() {
        return ParseQuery.getQuery(UnanimusGroup.class);
    }

    CgaContainer getCgaContainer() {
        return cgaContainer;
    }

    private CgaContainer cgaContainer;

    public UnanimusGroup() {
        super();
    }

    void load() throws ParseException {
        fetchIfNeeded();

        if (!has(CreateGroupActivity.CGA)
                || !has(VOTE_CONTAINERS)
                || !has(USER_IDS)
                || !has(RESTAURANT_IDS))
            throw new IllegalStateException();

        this.cgaContainer = (CgaContainer) get(CreateGroupActivity.CGA);

        cgaContainer.load();

        final List<VotesList> voteIds = getList(VOTE_CONTAINERS);
        final List<String> userIds = getList(USER_IDS);
        final List<String> parseRestaurantIds = getList(RESTAURANT_IDS);

        final int numUsers = userIds.size();

        if ((voteIds.size() != numUsers) || (userIds.size() != numUsers))
            throw new IllegalStateException(
                    "Received invalid data while attempting to initialize UnanimusGroup"
            );

        userIdsVc = new Hashtable<>(numUsers);

        for (int i = 0; numUsers > i; ++i) {
            final VotesList votesList;
            try {
                votesList = voteIds.get(i);
            } catch (NullPointerException e) {
                Log.i(UnanimusApplication.UNANIMUS, "Null voteList");
                Log.d(UnanimusApplication.UNANIMUS, e.getMessage(), e);
                continue;
            }

            votesList.load();

            userIdsVc.put(userIds.get(i), voteIds.get(i));
        }

        this.restaurantIds = new ImmutableList<>(parseRestaurantIds);

        this.recommendation = getList(RECOMMENDATION);
    }

    private UnanimusGroup(
            Map<String, VotesList> userIdsVc,
            List<String> restaurantIds,
            CgaContainer cgaContainer
    ) {
        this.userIdsVc = userIdsVc;
        this.restaurantIds = restaurantIds;
        this.cgaContainer = cgaContainer;

        commit();
    }

    ListIterator<String> getRestaurantIterator() {
        return restaurantIds.listIterator();
    }

    public boolean hasNotVoted() {
        return getMyVotes().contains(Integer.MIN_VALUE);
    }

    public VotesList getMyVotes() {
        return userIdsVc.get(ParseUser.getCurrentUser().getObjectId());
    }
    public Collection<VotesList> getUserIdsVs() {
        return userIdsVc.values();
    }

    void vote(
            final int index,
            @NonNull final Integer vote,
            @Nullable final SaveCallback saveCallback
    ) {
        final VotesList votesList = userIdsVc.get(
                ParseUser.getCurrentUser().getObjectId()
        );

        votesList.set(index, vote);
        votesList.saveInBackground(new SaveCallback() {
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
                                votesList.getObjectId()
                        )
                );

                if (saveCallback != null)
                    saveCallback.done(null);
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
        addAll(RECOMMENDATION, recommendation);

        put(CreateGroupActivity.CGA, cgaContainer);
    }

    public boolean allHaveVoted() {
        for (VotesList v : userIdsVc.values())
            if (v.contains(Integer.MIN_VALUE))
                return false;

        return true;
    }

    public static class Builder {
        private static final String[] BOOT_STRAP = new String[]{
                "ChIJh2E4tQIq9ocRmxkXDVB0zZQ",  //blue door
                "ChIJrSAGxzwq9ocRMEzwa0u133g",  //st clair broiler
                "ChIJw_ls8zwq9ocR6VGmxMFu3mc",  //acme deli
                "ChIJ894-6Ioq9ocRO1KIwauCOzE",  //cafe latte
                "ChIJQ5mqDBcq9ocRy0X2LCioShw"   //jamba juice
        };
        private final CgaContainer cgaContainer;

        public Builder(CgaContainer cgaContainer) {
            if (!cgaContainer.isSet())
                throw new IllegalArgumentException("CgaContainer is not set");

            this.cgaContainer = cgaContainer;
        }

        public void getInBackground(final Callback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!cgaContainer.isSet())
                        throw new IllegalStateException();

                    final ParseUser parseUser = ParseUser.getCurrentUser();

                    final int maxRestaurants = cgaContainer.getMaxRestaurants();

                    final FpaContainer fpaContainer = cgaContainer.getFpaContainer();
                    final PpaContainer ppaContainer = cgaContainer.getPpaContainer();
                    final GspaContainer gspaContainer = cgaContainer.getGspaContainer();

                    final FpaContainer.UserIdPair[] userIdPairs = fpaContainer.getUserIdPairs();

                    final LatLng latLng = ppaContainer.getLatLng();

                    final Date date = gspaContainer.getDate();
                    final int radius = gspaContainer.getRadius();
                    final GspaContainer.EPriceLevel ePriceLevel = gspaContainer.getPriceLevel();

                    final Map<String, VotesList> userIdsVc;
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

                        final VotesList votesList = new VotesList(
                                maxRestaurants,
                                parseUser,
                                voterId,
                                readers
                        );

                        userIdsVc.put(voterId, votesList);
                    }

                    callback.done(new UnanimusGroup(userIdsVc, restaurantIds, cgaContainer));
                }
            }).run();

        }

        interface Callback {
            void done(UnanimusGroup unanimusGroup);
        }
    }
}
