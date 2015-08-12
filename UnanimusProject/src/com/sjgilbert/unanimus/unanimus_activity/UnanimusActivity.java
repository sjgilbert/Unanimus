package com.sjgilbert.unanimus.unanimus_activity;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjgilbert.unanimus.R;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
class UnanimusActivity extends Activity {
    protected static boolean setTitleBar(String title, ViewGroup parent) {
        return setTitleBar(title, parent, R.id.activity_title_layout);
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
        return setTextEntryBar(hint, submit, parent, R.id.te_base);
    }
protected static boolean setTextEntryBar(String hint, String submit, ViewGroup parent, int base_id) {
        try {
            return setTextEntryBar(hint, submit, findTextEntryBase(parent, base_id));
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
                    findTextEntryEditText((ViewGroup) base.getParent()),
                    findTextEntryButton((ViewGroup) base.getParent()));
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

    protected static LinearLayout findTextEntryBase(ViewGroup parent) {
        return findTextEntryBase(parent, R.id.te_base);
    }

    protected static LinearLayout findTextEntryBase(ViewGroup parent, int base_id) {
        LinearLayout base = null;
        try {
            base = (LinearLayout) parent.findViewById(base_id);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return base;
    }

    protected static EditText findTextEntryEditText(ViewGroup parent) {
        return findTextEntryEditText(parent, R.id.te_base);
    }

    protected static EditText findTextEntryEditText(ViewGroup parent, int base_id) {
        LinearLayout base = findTextEntryBase(parent, base_id);
        EditText editText = null;
        try {
            editText = (EditText) base.findViewById(R.id.te_edit_text);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return editText;
    }

    protected static Button findTextEntryButton(ViewGroup parent) {
        return findTextEntryButton(parent, R.id.te_base);
    }

    protected static Button findTextEntryButton(ViewGroup parent, int base_id) {
        LinearLayout base = findTextEntryBase(parent, base_id);
        Button button = null;
        try {
            button = (Button) base.findViewById(R.id.te_button);
        } catch (NullPointerException | ClassCastException e) {
            e.printStackTrace();
        }
        return button;
    }
}
