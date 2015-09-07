package com.sjgilbert.unanimus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sjgilbert.unanimus.unanimus_activity.UnanimusActivityTitle;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

// TODO: lets get some licensing up in this joint

/**
 * Created by sam on 8/9/15.
 */
public class GroupSettingsPickerActivity extends UnanimusActivityTitle {
    final static String GSPA = "gspaContainer";
    final static GspaContainer.EPriceLevel PRICE_LEVEL_DEFAULT = GspaContainer.EPriceLevel.$$;
    private final static int RADIUS_MAX = 10;
    final static int RADIUS_PROGRESS_DEFAULT = RADIUS_MAX / 2;

    private final GspaContainer gspaContainer = new GspaContainer();

    public GroupSettingsPickerActivity() {
        super("gspa");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_settings_picker_activity);
        try {
            setTitleBar(R.string.gspa_title, (ViewGroup) findViewById(R.id.group_settings_picker_activity));
        } catch (ClassCastException e) {
            log(ELog.e, e.getMessage(), e);
        }

        try {
            gspaContainer.setDefault();
        } catch (IContainer.NotSetException e) {
            log(ELog.e, e.getMessage(), e);
        }

        Button dateButton = (Button) findViewById(R.id.gspa_date);
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDatePickerDialog();
                    }
                }
        );
        updateDateText();

        Button timeButton = (Button) findViewById(R.id.gspa_time);
        timeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTimePickerDialog();
                    }
                }
        );
        updateTimeText();

        final SeekBar radiusBar = (SeekBar) findViewById(R.id.gspa_radius_slider);
        radiusBar.setMax(RADIUS_MAX);
        radiusBar.setProgress(gspaContainer.getRadius());
        radiusBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setRadius(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        radiusBar.playSoundEffect(SoundEffectConstants.CLICK);
                        showRadius();
                    }
                }
        );

        RadioGroup priceGroup = (RadioGroup) findViewById(R.id.gspa_price_group);
        priceGroup.check(R.id.gspa_price_$);
        priceGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        try {
                            RadioButton checkedButton = (RadioButton) findViewById(checkedId);
                            setPriceLevel(checkedButton.getText().length());
                            checkedButton.playSoundEffect(SoundEffectConstants.CLICK);
                            showPriceLevel();
                        } catch (NullPointerException | ClassCastException e) {
                            log(ELog.e, e.getMessage(), e);
                        }
                    }
                }
        );
    }

    @SuppressWarnings("unused")
    public void gspa_viewSubmit(@SuppressWarnings("UnusedParameters") View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        int result;
        if (gspaContainer.isSet()) {
            try {
                intent.putExtra(GSPA, gspaContainer.getAsBundle());
            } catch (IContainer.NotSetException e) {
                log(ELog.e, e.getMessage(), e);
            }
            result = RESULT_OK;
        } else {
            result = RESULT_CANCELED;
        }
        setResult(result, intent);
        super.finish();
    }


    private Date getDate() {
        return gspaContainer.getDate();
    }

    private void setDate(int day, int month, int year) {
        gspaContainer.setYear(year);
        gspaContainer.setMonth(month);
        gspaContainer.setDay(day);
    }

    private String getDateString() {
        final Date date = getDate();

        DateFormat dateFormat = DateFormat.getDateInstance(
                DateFormat.DEFAULT,
                Locale.getDefault()
        );

        return String.format(
                Locale.getDefault(),
                "%s: %s",
                getString(R.string.gspa_date),
                dateFormat.format(date)
        );
    }

    private void updateDateText() {
        Button button = (Button) findViewById(R.id.gspa_date);
        button.setText(getDateString());
    }

    private void showDate() {
        Toast.makeText(
                GroupSettingsPickerActivity.this,
                getDateString(),
                Toast.LENGTH_LONG
        ).show();
    }

    private void startDatePickerDialog() {
        new DatePickerDialog(
                GroupSettingsPickerActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth
                    ) {
                        setDate(dayOfMonth, monthOfYear, year);
                        updateDateText();
                        showDate();
                    }
                },
                gspaContainer.getYear(),
                gspaContainer.getMonth(),
                gspaContainer.getDay()
        ).show();
    }

    private void setTime(
            int hourOfDay,
            int minute
    ) {
        gspaContainer.setHourOfDay(hourOfDay);
        gspaContainer.setMinute(minute);
    }

    private String getTimeString() {
        final Date date = getDate();

        DateFormat dateFormat = DateFormat.getTimeInstance(
                DateFormat.SHORT,
                Locale.getDefault()
        );

        return String.format(
                Locale.getDefault(),
                "%s: %s",
                getString(R.string.gspa_time),
                dateFormat.format(date)
        );
    }

    private void updateTimeText() {
        Button txtTime = (Button) findViewById(R.id.gspa_time);
        txtTime.setText(getTimeString());
    }

    private void showTime() {
        Toast.makeText(
                GroupSettingsPickerActivity.this,
                getTimeString(),
                Toast.LENGTH_LONG
        ).show();
    }

    private void startTimePickerDialog() {
        new TimePickerDialog(
                GroupSettingsPickerActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(
                            TimePicker view,
                            int hourOfDay,
                            int minute
                    ) {
                        setTime(hourOfDay, minute);
                        updateTimeText();
                        showTime();
                    }
                },
                gspaContainer.getHourOfDay(),
                gspaContainer.getMinute(),
                true
        ).show();
    }

    private void setRadius(int radius) {
        gspaContainer.setRadius(radius);
    }

    private void showRadius() {
        Toast.makeText(
                GroupSettingsPickerActivity.this,
                String.format(
                        "%s: %d",
                        getString(R.string.gspa_radius),
                        gspaContainer.getRadius()
                ),
                Toast.LENGTH_LONG
        ).show();
    }

    private void setPriceLevel(int len) {
        try {
            gspaContainer.setPriceLevel(
                    GspaContainer.EPriceLevel
                            .getPriceLevelFromInt(len)
            );
        } catch (UnsupportedOperationException e) {
            log(ELog.e, e.getMessage(), e);
        }
    }

    private void showPriceLevel() {
        Toast.makeText(
                GroupSettingsPickerActivity.this,
                String.format(
                        "%s: %s",
                        getString(R.string.gspa_price_level),
                        gspaContainer.getPriceLevel()
                ),
                Toast.LENGTH_LONG
        ).show();
    }

}
