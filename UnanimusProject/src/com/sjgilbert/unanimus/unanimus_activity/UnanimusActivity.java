package com.sjgilbert.unanimus.unanimus_activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sjgilbert.unanimus.R;

import java.util.Locale;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
@SuppressLint("Registered")
class UnanimusActivity extends Activity {
    private final String tag;

    UnanimusActivity(String tag) {
        this.tag = String.format(
                Locale.getDefault(),
                "%s/%s",
                "Unanimus",
                tag
        );
    }

    // TODO: finish this tag shit
    static boolean setTitleBar(String title, ViewGroup parent) {
        return setTitleBar(title, parent, R.id.activity_title_layout);
    }

    static boolean setTitleBar(String title, ViewGroup parent, int base_id) {
        try {
            return setTitleBar(title, (LinearLayout) parent.findViewById(base_id));
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static boolean setTitleBar(String title, LinearLayout base) {
        try {
            return setTitleBar(title, (TextView) base.findViewById(R.id.at_text));
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static boolean setTitleBar(String title, TextView textView) {
        try {
            textView.setText(title);
            return false;
        } catch (NullPointerException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static boolean setTextEntryBar(String hint, String submit, ViewGroup parent) {
        return setTextEntryBar(hint, submit, parent, R.id.te_base);
    }

    static boolean setTextEntryBar(String hint, String submit, ViewGroup parent, int base_id) {
        try {
            return setTextEntryBar(hint, submit, findTextEntryBase(parent, base_id));
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static boolean setTextEntryBar(String hint, String submit, LinearLayout base) {
        try {
            return setTextEntryBar(
                    hint,
                    submit,
                    findTextEntryEditText((ViewGroup) base.getParent()),
                    findTextEntryButton((ViewGroup) base.getParent()));
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static boolean setTextEntryBar(String hint, String submit, TextView textView, Button button) {
        try {
            textView.setHint(hint);
        } catch (NullPointerException e) {
            Log.e("Unanimus", e.getMessage(), e);
            return true;
        }
        try {
            button.setText(submit);
            return false;
        } catch (NullPointerException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return true;
    }

    static LinearLayout findTextEntryBase(ViewGroup parent) {
        return findTextEntryBase(parent, R.id.te_base);
    }

    static LinearLayout findTextEntryBase(ViewGroup parent, int base_id) {
        LinearLayout base = null;
        try {
            base = (LinearLayout) parent.findViewById(base_id);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return base;
    }

    static EditText findTextEntryEditText(ViewGroup parent) {
        return findTextEntryEditText(parent, R.id.te_base);
    }

    static EditText findTextEntryEditText(ViewGroup parent, int base_id) {
        LinearLayout base = findTextEntryBase(parent, base_id);
        EditText editText = null;
        try {
            editText = (EditText) base.findViewById(R.id.te_edit_text);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return editText;
    }

    static Button findTextEntryButton(ViewGroup parent) {
        return findTextEntryButton(parent, R.id.te_base);
    }

    static Button findTextEntryButton(ViewGroup parent, int base_id) {
        LinearLayout base = findTextEntryBase(parent, base_id);
        Button button = null;
        try {
            button = (Button) base.findViewById(R.id.te_button);
        } catch (NullPointerException | ClassCastException e) {
            Log.e("Unanimus", e.getMessage(), e);
        }
        return button;
    }

    protected int log(ELog log, String message) {
        return log.logger.log(this.tag, message);
    }

    protected int log(ELog log, String message, Throwable e) {
        return log.logger.log(this.tag, message, e);
    }


    public enum ELog {
        e(new Logger() {
            @Override
            public int log(String tag, String message) {
                return Log.e(tag, message);
            }

            @Override
            public int log(String tag, String message, Throwable throwable) {
                return Log.e(tag, message, throwable);
            }
        }),
        w(new Logger() {
            @Override
            public int log(String tag, String message) {
                return Log.w(tag, message);
            }

            @Override
            public int log(String tag, String message, Throwable throwable) {
                return Log.w(tag, message, throwable);
            }
        }),
        i(new Logger() {
            @Override
            public int log(String tag, String message) {
                return Log.i(tag, message);
            }

            @Override
            public int log(String tag, String message, Throwable throwable) {
                return Log.i(tag, message, throwable);
            }
        }),
        d(new Logger() {
            @Override
            public int log(String tag, String message) {
                return Log.d(tag, message);
            }

            @Override
            public int log(String tag, String message, Throwable throwable) {
                return Log.d(tag, message, throwable);
            }
        }),
        v(new Logger() {
            @Override
            public int log(String tag, String message) {
                return Log.v(tag, message);
            }

            @Override
            public int log(String tag, String message, Throwable throwable) {
                return Log.v(tag, message, throwable);
            }
        });

        private final Logger logger;

        ELog(Logger logger) {
            this.logger = logger;
        }

        private interface Logger {
            int log(String tag, String message);

            int log(String tag, String message, Throwable throwable);
        }
    }
}
