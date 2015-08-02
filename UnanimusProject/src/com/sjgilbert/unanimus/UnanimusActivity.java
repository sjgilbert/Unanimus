package com.sjgilbert.unanimus;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

/**
 * 8/1/15 (c) Isabell Cowan
 * isabellcowan@gmail.com
 */
abstract class UnanimusActivity extends Activity {
    public void setTitle(int title_id, View content) {
        if (null == content) {
            new NullPointerException().printStackTrace();
            return;
        }
        View activityTitleLayout = content.findViewById(R.id.activity_title_layout);
        if (null == activityTitleLayout) {
            new NullPointerException().printStackTrace();
            return;
        }
        View activityTitleText = activityTitleLayout.findViewById(R.id.activity_title_text);
        if (null == activityTitleText) {
            new NullPointerException().printStackTrace();
            return;
        }
        TextView textView;
        if (activityTitleText instanceof TextView) {
            textView = (TextView) activityTitleText;
        } else {
            new ClassCastException().printStackTrace();
            return;
        }
        textView.setText(getResources().getText(title_id));
    }
}
