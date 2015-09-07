package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@ParseClassName("FpaContainer")
public class FpaContainer extends ParseObject implements IContainer {
    private final static String FACEBOOK_IDS = "facebookIds";
    private final static String PARSE_IDS = "parseIds";

    private UserIdPair[] userIdPairs;

    public FpaContainer() {
        super();
    }

    UserIdPair[] getUserIdPairs() {
        return userIdPairs;
    }

    void setUserIdPairs(UserIdPair[] userIdPairs) {
        this.userIdPairs = userIdPairs;
    }

    @Override
    public void commit() throws NotSetException {
        final List<String> facebookIds = new ArrayList<>();
        final List<String> parseIds = new ArrayList<>();

        setLists(parseIds, facebookIds);

        put(FACEBOOK_IDS, facebookIds);
        put(PARSE_IDS, parseIds);
    }


    @Override
    public boolean isSet() {
        return (userIdPairs != null && 0 < userIdPairs.length);
    }

    @Override
    public void load() throws ParseException {
        fetchIfNeeded();

        if (!has(FACEBOOK_IDS) || !has(PARSE_IDS))
            return;

        final List<String> facebookIds = getList(FACEBOOK_IDS);
        final List<String> parseIds = getList(PARSE_IDS);

        if (null == facebookIds || null == parseIds)
            return;

        if (facebookIds.size() != parseIds.size())
            throw new IllegalStateException();

        final int size = facebookIds.size();

        this.userIdPairs = new UserIdPair[size];

        for (int i = 0; size > i; ++i)
            userIdPairs[i] = new UserIdPair(facebookIds.get(i), parseIds.get(i));
    }

    @Override
    public Bundle getAsBundle() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        Bundle bundle = new Bundle();

        final ArrayList<String> facebookUserIds = new ArrayList<>();
        final ArrayList<String> parseUserIds = new ArrayList<>();

        setLists(parseUserIds, facebookUserIds);

        bundle.putStringArrayList(FACEBOOK_IDS, facebookUserIds);
        bundle.putStringArrayList(PARSE_IDS, parseUserIds);

        bundle.putString(ParseCache.OBJECT_ID, getObjectId());

        return bundle;
    }

    @Override
    public void setDefault() {
        final String userFacebookID = ParseUser.getCurrentUser().getString(FriendPickerActivity.FACEBOOK_ID);
        final String userParseId = ParseUser.getCurrentUser().getObjectId();

        userIdPairs = new UserIdPair[]{new UserIdPair(userFacebookID, userParseId)};
    }

    @Override
    public void setFromBundle(Bundle bundle) throws NotSetException {
        final ArrayList<String> facebookIds = bundle.getStringArrayList(FACEBOOK_IDS);
        final ArrayList<String> parseUserIds = bundle.getStringArrayList(PARSE_IDS);

        assert parseUserIds != null;
        assert facebookIds != null;

        if (parseUserIds.size() != facebookIds.size())
            throw new IllegalArgumentException();

        userIdPairs = new UserIdPair[facebookIds.size()];

        for (int i = 0; userIdPairs.length > i; ++i)
            userIdPairs[i] = new UserIdPair(facebookIds.get(i), parseUserIds.get(i));

        setObjectId(bundle.getString(ParseCache.OBJECT_ID));
        commit();
    }

    private void setLists(List<String> parseUserIds, List<String> facebookUserIds) {
        for (UserIdPair pair : userIdPairs) {
            if (null == pair.parseUserId) {
                Log.w(
                        "Unanimus/" + FriendPickerActivity.TAG,
                        "Missing parse user id in a selected friend, skipping . . . "
                );

                continue;
            }

            facebookUserIds.add(pair.facebookUserId);
            parseUserIds.add(pair.parseUserId);
        }
    }

    public static class UserIdPair {
        public final String facebookUserId;
        public final String parseUserId;

        UserIdPair(String facebookUserId, String parseUserId) {
            this.facebookUserId = facebookUserId;
            this.parseUserId = parseUserId;
        }
    }
}


