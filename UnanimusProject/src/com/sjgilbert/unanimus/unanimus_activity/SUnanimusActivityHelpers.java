package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjgilbert.unanimus.R;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
/* private static */ class SUnanimusActivityHelpers {
    protected static boolean setTitle(String title, ViewGroup parent) {
        try {
            setTitle(title, parent, R.id.activity_title_layout);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    protected static boolean setTitle(String title, ViewGroup parent, int base_id) {
        try {
            setTitle(title, (LinearLayout) parent.findViewById(base_id));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    protected static boolean setTitle(String title, LinearLayout base) {
        try {
            setTitle(title, (TextView) base.findViewById(R.id.activity_title_text));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
    protected static boolean setTitle(String title, TextView textView) {
        try {
            textView.setText(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
