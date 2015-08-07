package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public interface IUnanimusTitle {
    boolean setTitleBar(int title_r, ViewGroup parent);

    boolean setTitleBar(int title_r, ViewGroup parent, int base_id);

    boolean setTitleBar(int title_r, LinearLayout base);

    boolean setTitleBar(int title_r, TextView textView);
}
