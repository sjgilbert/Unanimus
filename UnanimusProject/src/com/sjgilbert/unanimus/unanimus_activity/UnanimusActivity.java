package com.sjgilbert.unanimus.unanimus_activity;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjgilbert.unanimus.R;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
class UnanimusActivity extends Activity {
    protected static boolean setTitleBar(String title, ViewGroup parent) {
        return setTitleBar(title, parent, R.id.at_layout);
    }

    protected static boolean setTitleBar(String title, ViewGroup parent, int base_id) {
        try {
            return setTitleBar(title, (LinearLayout) parent.findViewById(base_id));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected static boolean setTitleBar(String title, LinearLayout base) {
        try {
            return setTitleBar(title, (TextView) base.findViewById(R.id.at_text));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected static boolean setTitleBar(String title, TextView textView) {
        try {
            textView.setText(title);
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected static boolean setTextEntryBar(String hint, String submit, ViewGroup parent) {
        return setTextEntryBar(hint, submit, parent, R.id.text_entry_bar);
    }

    protected static boolean setTextEntryBar(String hint, String submit, ViewGroup parent, int base_id) {
        try {
            return setTextEntryBar(hint, submit, (LinearLayout) parent.findViewById(base_id));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected static boolean setTextEntryBar(String hint, String submit, LinearLayout base) {
        try {
            return setTextEntryBar(
                    hint,
                    submit,
                    (TextView) base.findViewById(R.id.te_text_field),
                    (Button) base.findViewById(R.id.te_submit_button));
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected static boolean setTextEntryBar(String hint, String submit, TextView textView, Button button) {
        try {
            textView.setHint(hint);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return true;
        }
        try {
            button.setText(submit);
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }
}
