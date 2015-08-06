package com.sjgilbert.unanimus.unanimus_activity;

import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public abstract class UnanimusActivityTitle extends UnanimusActivity implements IUnanimusTitle {
    @Override
    public void setUnanimusTitle(int title_r) {
        setUnanimusTitle(getResources().getString(title_r));
    }

    @Override
    public void setUnanimusTitle(String title_s) {
        title = title_s;
    }

    @Override
    public String getUnanimusTitle() {
        return title;
    }

    @Override
    public boolean setTitleBar(ViewGroup parent) {
        return SUnanimusActivityHelpers.setTitle(title, parent);
    }

    @Override
    public boolean setTitleBar(ViewGroup parent, int base_id) {
        return SUnanimusActivityHelpers.setTitle(title, parent, base_id);
    }

    @Override
    public boolean setTitleBar(LinearLayout base) {
        return SUnanimusActivityHelpers.setTitle(title, base);
    }

    @Override
    public boolean setTitleBar(TextView textView) {
        return SUnanimusActivityHelpers.setTitle(title, textView);
    }

    private String title;
}
