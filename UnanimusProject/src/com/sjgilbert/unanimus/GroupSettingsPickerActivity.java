package com.sjgilbert.unanimus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.util.Calendar;

/**
 * Created by sam on 8/9/15.
 */
public class GroupSettingsPickerActivity extends UnanimusActivityTitle{
    int mYear;
    int mMonth;
    int mDay;
    int mHourOfDay;
    int mMinute;
    int radius;
    int priceLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_settings_picker_activity);
        try {
            setTitleBar(R.string.gspa_title, (ViewGroup) findViewById(R.id.group_settings_picker_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        Button txtTime = (Button) findViewById(R.id.gspa_time);
        txtTime.setText("Time: " + mHourOfDay + ":" + mMinute);
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = new TimePickerDialog(GroupSettingsPickerActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mHourOfDay = hourOfDay;
                                mMinute = minute;
                                Button txtTime = (Button) findViewById(R.id.gspa_time);
                                txtTime.setText("Time: " + hourOfDay + ":"
                                        + minute);

                            }
                        }, mHourOfDay, mMinute, true);
                dpd.show();
            }
        });

        SeekBar radiusBar = (SeekBar) findViewById(R.id.gspa_radius_slider);
        radiusBar.setMax(10);
        radiusBar.setProgress(5);
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress;
                System.out.println(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(GroupSettingsPickerActivity.this, "Radius: " + seekBar.getProgress(), Toast.LENGTH_LONG).show();
            }
        });

        RadioGroup priceGroup = (RadioGroup) findViewById(R.id.gspa_price_group);
        priceGroup.check(R.id.gspa_price_$);
        priceLevel = 1;
        priceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                priceLevel = checkedButton.getText().length();
                System.out.println(priceLevel);
            }
        });

        Button submitButton = (Button) findViewById(R.id.gspa_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Date: " + mDay + "-" + (mMonth + 1) + "-" + mYear);
                System.out.println("Time: " + mHourOfDay + ":" + mMinute);
                System.out.println("Radius: " + radius);
                System.out.println("Price Level: " + priceLevel);
            }
        });

        Button txtDate = (Button) findViewById(R.id.gspa_date);
        txtDate.setText("Date: " + mDay + "-" + (mMonth + 1) + "-" + mYear);
        txtDate.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick (View v){
                                           DatePickerDialog dpd = new DatePickerDialog(GroupSettingsPickerActivity.this,
                                                   new DatePickerDialog.OnDateSetListener() {

                                                       @Override
                                                       public void onDateSet(DatePicker view, int year,
                                                                             int monthOfYear, int dayOfMonth) {
                                                           mYear = year;
                                                           mMonth = monthOfYear;
                                                           mDay = dayOfMonth;
                                                           Button txtDate = (Button) findViewById(R.id.gspa_date);
                                                           txtDate.setText("Date: " + dayOfMonth + "-"
                                                                   + (monthOfYear + 1) + "-" + year);

                                                       }
                                                   }, mYear, mMonth, mDay);
                                           dpd.show();
                                       }
                                   }

        );
    }

}
