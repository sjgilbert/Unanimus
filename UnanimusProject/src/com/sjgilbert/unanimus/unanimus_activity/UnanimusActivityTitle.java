package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public abstract class UnanimusActivityTitle extends UnanimusActivity implements IUnanimusTitle {
    protected UnanimusActivityTitle(String tag) {
        super(tag);
    }

    @Override
    public boolean setTitleBar(int title_r, ViewGroup parent) {
        return UnanimusActivity.setTitleBar(getResources().getString(title_r), parent, this.tag);
    }

    @Override
    public boolean setTitleBar(int title_r, ViewGroup parent, int base_id) {
        return UnanimusActivity.setTitleBar(getResources().getString(title_r), parent, base_id);
    }

    @Override
    public boolean setTitleBar(int title_r, LinearLayout base) {
        return UnanimusActivity.setTitleBar(getResources().getString(title_r), base);
    }

    @Override
    public boolean setTitleBar(int title_r, TextView textView) {
        return UnanimusActivity.setTitleBar(getResources().getString(title_r), textView);
    }
}
