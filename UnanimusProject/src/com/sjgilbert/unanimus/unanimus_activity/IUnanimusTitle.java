package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public interface IUnanimusTitle {
    void setUnanimusTitle(int title_r);
    void setUnanimusTitle(String title_s);
    String getUnanimusTitle();
    boolean setTitleBar(ViewGroup parent);
    boolean setTitleBar(ViewGroup parent, int base_id);
    boolean setTitleBar(LinearLayout base);
    boolean setTitleBar(TextView textView);
}
