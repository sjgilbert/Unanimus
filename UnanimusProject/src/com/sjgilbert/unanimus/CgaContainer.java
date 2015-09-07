package com.sjgilbert.unanimus;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.ArrayList;

/**
 * Model for a group_activity of users.
 */
@ParseClassName("CgaContainer")
public class CgaContainer extends ParseObject implements IContainer {
    static final String OWNER_ID = "ownerId";

    private String ownerId;

    private FpaContainer fpaContainer;
    private GspaContainer gspaContainer;
    private PpaContainer ppaContainer;

    public CgaContainer() {
        super();
    }

    CgaContainer(String ownerId) {
        this.ownerId = ownerId;
    }

    static ParseQuery<CgaContainer> getQuery() {
        return ParseQuery.getQuery(CgaContainer.class);
    }

    int getMaxRestaurants() {
        return 15;
    }

    @Nullable
    GspaContainer getGspaContainer() {
        return gspaContainer;
    }

    void setGspaContainer(Bundle bundle) throws NotSetException {
        this.gspaContainer = new GspaContainer();
        gspaContainer.setFromBundle(bundle);
    }

    @Nullable
    FpaContainer getFpaContainer() {
        return fpaContainer;
    }

    void setFpaContainer(Bundle bundle) throws NotSetException {
        this.fpaContainer = new FpaContainer();
        fpaContainer.setFromBundle(bundle);
    }

    @Nullable
    PpaContainer getPpaContainer() {
        return ppaContainer;
    }

    void setPpaContainer(Bundle bundle) throws NotSetException {
        this.ppaContainer = new PpaContainer();
        ppaContainer.setFromBundle(bundle);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Deprecated
    public ArrayList<String> getMembers() {
        FpaContainer.UserIdPair[] userIdPairs = fpaContainer.getUserIdPairs();

        ArrayList<String> list = new ArrayList<>(userIdPairs.length);

        for (FpaContainer.UserIdPair pair : userIdPairs) list.add(pair.facebookUserId);

        return list;
    }

    @Override
    public Bundle getAsBundle() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        Bundle bundle = new Bundle();

        bundle.putBundle(GroupSettingsPickerActivity.GSPA, gspaContainer.getAsBundle());
        bundle.putBundle(FriendPickerActivity.FPA, fpaContainer.getAsBundle());
        bundle.putBundle(PlacePickActivity.PPA, ppaContainer.getAsBundle());

        bundle.putString(ParseCache.OBJECT_ID, getObjectId());

        bundle.putString(OWNER_ID, ownerId);

        return bundle;
    }

    @Override
    public void commit() throws NotSetException {
        gspaContainer.commit();
        fpaContainer.commit();
        ppaContainer.commit();

        put(GroupSettingsPickerActivity.GSPA, gspaContainer);
        put(FriendPickerActivity.FPA, fpaContainer);
        put(PlacePickActivity.PPA, ppaContainer);

        put(OWNER_ID, ownerId);
    }

    @Override
    public void setDefault() throws NotSetException {
        gspaContainer = new GspaContainer();
        fpaContainer = new FpaContainer();
        ppaContainer = new PpaContainer();

        ownerId = ParseUser.getCurrentUser().getObjectId();
    }

    @Override
    public void setFromBundle(Bundle bundle) throws NotSetException {
        setGspaContainer(bundle.getBundle(GroupSettingsPickerActivity.GSPA));
        setFpaContainer(bundle.getBundle(FriendPickerActivity.FPA));
        setPpaContainer(bundle.getBundle(PlacePickActivity.PPA));

        setObjectId(bundle.getString(ParseCache.OBJECT_ID));

        setOwnerId(bundle.getString(OWNER_ID));

        commit();
    }

    @Override
    public boolean isSet() {
        return (ppaContainer != null
                && ppaContainer.isSet()
                && fpaContainer != null
                && fpaContainer.isSet()
                && gspaContainer != null
                && gspaContainer.isSet()
                && ownerId != null
        );
    }

    @Override
    public void load() throws ParseException {
        if (!has(FriendPickerActivity.FPA)
                || !has(GroupSettingsPickerActivity.GSPA)
                || !has(PlacePickActivity.PPA))
            throw new IllegalStateException();

        fpaContainer = (FpaContainer) getParseObject(FriendPickerActivity.FPA);
        gspaContainer = (GspaContainer) getParseObject(GroupSettingsPickerActivity.GSPA);
        ppaContainer = (PpaContainer) getParseObject(PlacePickActivity.PPA);

        ownerId = getString(OWNER_ID);

        fpaContainer.load();
        gspaContainer.load();
        ppaContainer.load();
    }

}
