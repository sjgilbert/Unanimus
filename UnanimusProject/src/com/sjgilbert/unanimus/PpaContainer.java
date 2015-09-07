package com.sjgilbert.unanimus;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.sjgilbert.unanimus.parsecache.ParseCache;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@ParseClassName("PpaContainer")
public class PpaContainer extends ParseObject implements IContainer {
    private static final String LAT = "lat";
    private static final String LNG = "lng";

    private LatLng latLng;

    public PpaContainer() {
        super();
    }

    @Override
    public Bundle getAsBundle() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        Bundle bundle = new Bundle();
        bundle.putDouble(LAT, latLng.latitude);
        bundle.putDouble(LNG, latLng.longitude);

        bundle.putString(ParseCache.OBJECT_ID, getObjectId());

        return bundle;
    }

    @Override
    public void commit() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        put(LAT, latLng.latitude);
        put(LNG, latLng.longitude);
    }

    @Override
    public void setDefault() {
        latLng = new LatLng(0D, 0D);
    }

    @Override
    public void setFromBundle(Bundle bundle) throws NotSetException {
        setFromLatLng(
                new LatLng(
                        bundle.getDouble(LAT),
                        bundle.getDouble(LNG)
                )
        );

        setObjectId(bundle.getString(ParseCache.OBJECT_ID));
        commit();
    }

    @Override
    public boolean isSet() {
        return null != latLng;
    }

    @Override
    public void load() throws ParseException {
        fetchIfNeeded();

        if (!has(LAT) || !has(LNG))
            return;

        int lat = getInt(LAT);
        int lng = getInt(LNG);

        this.latLng = new LatLng(lat, lng);
    }

    void setFromLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    LatLng getLatLng() {
        return latLng;
    }
}

