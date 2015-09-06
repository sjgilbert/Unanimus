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

@SuppressWarnings("unused")
interface IUnanimusTextEntryBar {

    boolean setTextEntryBar(int hint_r, int submit_r, ViewGroup parent);

    boolean setTextEntryBar(int hint_r, int submit_r, ViewGroup parent, int base_id);

    boolean setTextEntryBar(int hint_r, int submit_r, LinearLayout base);

    boolean setTextEntryBar(int hint_r, int submit_r, TextView textView, Button button);

    LinearLayout getTextEntryBase(ViewGroup parent);

    LinearLayout getTextEntryBase(ViewGroup parent, int base_id);

    EditText getTextEntryEditText(ViewGroup parent);

    EditText getTextEntryEditText(ViewGroup parent, int base_id);

    Button getTextEntryButton(ViewGroup parent);

    Button getTextEntryButton(ViewGroup parent, int base_id);
}
