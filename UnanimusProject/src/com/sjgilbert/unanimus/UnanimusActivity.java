package com.sjgilbert.unanimus;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
abstract class UnanimusActivity extends Activity {
    public void setTitle(int title_id, View v) {
        ((TextView) v.findViewById(R.id.activity_title_layout).findViewById(R.id.activity_title_text)).setText(getResources().getText(title_id));
    }
}
