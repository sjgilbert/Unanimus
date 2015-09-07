package com.sjgilbert.unanimus;

import android.os.Bundle;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.sjgilbert.unanimus.parsecache.ParseCache;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 9/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@ParseClassName("GspaContainer")
public class GspaContainer extends ParseObject implements IContainer {
    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String HOUR_OF_DAY = "hourOfDay";
    private final static String MINUTE = "minute";
    private final static String RADIUS = "radius";
    private final static String PRICE_LEVEL = "priceLevel";

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hourOfDay = -1;
    private int minute = -1;

    private int radius = -1;

    private EPriceLevel priceLevel;

    @Override
    public void setDefault() throws NotSetException {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        radius = GroupSettingsPickerActivity.RADIUS_PROGRESS_DEFAULT;

        priceLevel = GroupSettingsPickerActivity.PRICE_LEVEL_DEFAULT;

        commit();
    }

    @Override
    public Bundle getAsBundle() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        Bundle bundle = new Bundle();

        bundle.putInt(YEAR, year);
        bundle.putInt(MONTH, month);
        bundle.putInt(DAY, day);
        bundle.putInt(HOUR_OF_DAY, hourOfDay);
        bundle.putInt(MINUTE, minute);

        bundle.putInt(RADIUS, radius);

        bundle.putInt(PRICE_LEVEL, priceLevel.getNum());

        bundle.putString(ParseCache.OBJECT_ID, getObjectId());

        return bundle;
    }

    @Override
    public void commit() throws NotSetException {
        if (!isSet())
            throw new NotSetException();

        put(YEAR, year);
        put(MONTH, month);
        put(DAY, day);
        put(HOUR_OF_DAY, hourOfDay);
        put(MINUTE, minute);

        put(RADIUS, radius);
        put(PRICE_LEVEL, priceLevel.getNum());
    }

    @Override
    public void setFromBundle(Bundle bundle) throws NotSetException {
        this.year = bundle.getInt(YEAR);
        this.month = bundle.getInt(MONTH);
        this.day = bundle.getInt(DAY);
        this.hourOfDay = bundle.getInt(HOUR_OF_DAY);
        this.minute = bundle.getInt(MINUTE);
        this.radius = bundle.getInt(RADIUS);
        this.priceLevel = EPriceLevel.getPriceLevelFromInt(bundle.getInt(PRICE_LEVEL));

        setObjectId(bundle.getString(ParseCache.OBJECT_ID));
        commit();
    }

    @Override
    public boolean isSet() {
        return ((year >= 0)
                && (month >= 0)
                && (day >= 0)
                && (hourOfDay >= 0)
                && (minute >= 0)
                && (radius >= 0)
                && (priceLevel != null)
        );
    }

    @Override
    public void load() throws ParseException {
        fetchIfNeeded();

        if (!has(YEAR)
                || !has(MONTH)
                || !has(DAY)
                || !has(MINUTE)
                || !has(RADIUS)
                || !has(PRICE_LEVEL))
            return;

        this.year = getInt(YEAR);
        this.month = getInt(MONTH);
        this.day = getInt(DAY);
        this.hourOfDay = getInt(HOUR_OF_DAY);
        this.minute = getInt(MINUTE);

        this.radius = getInt(RADIUS);

        this.priceLevel = EPriceLevel.getPriceLevelFromInt(getInt(PRICE_LEVEL));
    }

    @Deprecated
    public int getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }

    @Deprecated
    public int getMonth() {
        return month;
    }

    void setMonth(int month) {
        this.month = month;
    }

    @Deprecated
    public int getDay() {
        return day;
    }

    void setDay(int day) {
        this.day = day;
    }

    @Deprecated
    public int getHourOfDay() {
        return hourOfDay;
    }

    void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    @Deprecated
    public int getMinute() {
        return minute;
    }

    void setMinute(int minute) {
        this.minute = minute;
    }

    public int getRadius() {
        return radius;
    }

    void setRadius(int radius) {
        this.radius = radius;
    }

    public EPriceLevel getPriceLevel() {
        return priceLevel;
    }

    void setPriceLevel(EPriceLevel priceLevel) {
        this.priceLevel = priceLevel;
    }

    public Date getDate() {
        final Calendar calender = new GregorianCalendar(
                year,
                month,
                day,
                hourOfDay,
                minute
        );

        return calender.getTime();
    }

    public enum EPriceLevel {
        // funny as shit right?
        $(1),
        $$(2),
        $$$(3),
        $$$$(4);

        final int num;

        EPriceLevel(int num) {
            this.num = num;
        }

        public static EPriceLevel getPriceLevelFromInt(int len) {
            switch (len) {
                case 1:
                    return GspaContainer.EPriceLevel.$;
                case 2:
                    return GspaContainer.EPriceLevel.$$;
                case 3:
                    return GspaContainer.EPriceLevel.$$$;
                case 4:
                    return GspaContainer.EPriceLevel.$$$$;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public int getNum() {
            return num;
        }
    }
}

