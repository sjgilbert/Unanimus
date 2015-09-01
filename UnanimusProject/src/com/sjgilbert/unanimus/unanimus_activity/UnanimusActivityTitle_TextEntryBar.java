package com.sjgilbert.unanimus.unanimus_activity;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 8/6/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
public abstract class UnanimusActivityTitle_TextEntryBar
        extends UnanimusActivityTitle
        implements IUnanimusTitle, IUnanimusTextEntryBar {
    protected UnanimusActivityTitle_TextEntryBar(String tag) {
        super(tag);
    }

    @Override
    public boolean setTitleBar(int title_r, ViewGroup parent) {
        return UnanimusActivity.setTitleBar(getResources().getString(title_r), parent);
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

    @Override
    public LinearLayout getTextEntryBase(ViewGroup parent) {
        return findTextEntryBase(parent);
    }

    @Override
    public LinearLayout getTextEntryBase(ViewGroup parent, int base_id) {
        return findTextEntryBase(parent, base_id);
    }

    @Override
    public EditText getTextEntryEditText(ViewGroup parent) {
        return findTextEntryEditText(parent);
    }

    @Override
    public EditText getTextEntryEditText(ViewGroup parent, int base_id) {
        return findTextEntryEditText(parent, base_id);
    }

    @Override
    public Button getTextEntryButton(ViewGroup parent) {
        return findTextEntryButton(parent);
    }

    @Override
    public Button getTextEntryButton(ViewGroup parent, int base_id) {
        return findTextEntryButton(parent, base_id);
    }
}
