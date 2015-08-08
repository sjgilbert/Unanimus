package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public class UnanimusActivityTitle_TextEntryBar extends UnanimusActivityTitle implements IUnanimusTextEntryBar {
    @Override
    public boolean setTextEntryBar(int hint_r, int submit_r, ViewGroup parent) {
        return setTextEntryBar(getResources().getString(hint_r), getResources().getString(submit_r), parent);
    }

    @Override
    public boolean setTextEntryBar(int hint_r, int submit_r, ViewGroup parent, int base_id) {
        return setTextEntryBar(getResources().getString(hint_r), getResources().getString(submit_r), parent, base_id);
    }

    @Override
    public boolean setTextEntryBar(int hint_r, int submit_r, LinearLayout base) {
        return setTextEntryBar(getResources().getString(hint_r), getResources().getString(submit_r), base);
    }

    @Override
    public boolean setTextEntryBar(int hint_r, int submit_r, TextView textView, Button button) {
        return setTextEntryBar(getResources().getString(hint_r), getResources().getString(submit_r), textView, button);
    }
}
