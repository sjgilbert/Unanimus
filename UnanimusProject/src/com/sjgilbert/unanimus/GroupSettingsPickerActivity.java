package com.sjgilbert.unanimus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sam on 8/9/15.
 */
public class GroupSettingsPickerActivity extends UnanimusActivityTitle {
    private final static int radiusMax = 10;
    private final static int radiusStartProgress = radiusMax / 2;
    private final static GspaContainer.EPriceLevel startPriceLevel = GspaContainer.EPriceLevel.$$;

    private GspaContainer gspaContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_settings_picker_activity);
        try {
            setTitleBar(R.string.gspa_title, (ViewGroup) findViewById(R.id.group_settings_picker_activity));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        gspaContainer = new GspaContainer();

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

        SeekBar radiusBar = (SeekBar) findViewById(R.id.gspa_radius_slider);
        radiusBar.setMax(radiusMax);
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
                            showPriceLevel();
                        } catch (NullPointerException | ClassCastException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Button submitButton = (Button) findViewById(R.id.gspa_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntentFinish();
            }
        });
    }

    private void setDate(int day, int month, int year) {
        gspaContainer.year = year;
        gspaContainer.month = month;
        gspaContainer.day = day;
    }

    private String getDateString() {
        return String.format(
                Locale.getDefault(),
                "%s: %d-%d-%d",
                getString(R.string.gspa_date),
                gspaContainer.getDay(),
                gspaContainer.getMonth() + 1,
                gspaContainer.getYear()
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
        gspaContainer.hourOfDay = hourOfDay;
        gspaContainer.minute = minute;
    }

    private String getTimeString() {
        return String.format(
                Locale.getDefault(),
                "%s: %d:%d",
                getString(R.string.gspa_time),
                gspaContainer.getHourOfDay(),
                gspaContainer.getMinute()
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
        gspaContainer.radius = radius;
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
            gspaContainer.priceLevel = GspaContainer.EPriceLevel.getPriceLevelFromInt(len);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
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

    private void returnIntentFinish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("gspaContainer", gspaContainer.getAsBundle());
        setResult(RESULT_OK);
        finish();
    }

    protected static class GspaContainer {
        public final static String YEAR = "year";
        public final static String MONTH = "month";
        public final static String DAY = "day";
        public final static String HOUR_OF_DAY = "hourOfDay";
        public final static String MINUTE = "minute";
        public final static String RADIUS = "radius";
        public final static String PRICE_LEVEL = "priceLevel";

        private int year, month, day, hourOfDay, minute;
        private int radius;
        private EPriceLevel priceLevel;

        public GspaContainer() {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            radius = radiusStartProgress;

            priceLevel = startPriceLevel;
        }

        public GspaContainer(Bundle retIntVals) {
            this.year = retIntVals.getInt(YEAR);
            this.month = retIntVals.getInt(MONTH);
            this.day = retIntVals.getInt(DAY);
            this.hourOfDay = retIntVals.getInt(HOUR_OF_DAY);
            this.minute = retIntVals.getInt(MINUTE);
            this.radius = retIntVals.getInt(RADIUS);
            this.priceLevel = EPriceLevel.getPriceLevelFromInt(retIntVals.getInt(PRICE_LEVEL));
        }

        public Bundle getAsBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt(YEAR, year);
            bundle.putInt(MONTH, month);
            bundle.putInt(DAY, day);
            bundle.putInt(HOUR_OF_DAY, hourOfDay);
            bundle.putInt(MINUTE, minute);

            bundle.putInt(RADIUS, radius);

            bundle.putInt(PRICE_LEVEL, priceLevel.getNum());

            return bundle;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getHourOfDay() {
            return hourOfDay;
        }

        public int getMinute() {
            return minute;
        }

        public int getRadius() {
            return radius;
        }

        public EPriceLevel getPriceLevel() {
            return priceLevel;
        }

        public enum EPriceLevel {
            // funny as shit right?
            $(0),
            $$(1),
            $$$(2),
            $$$$(3);

            final int num;

            EPriceLevel(int num) {
                this.num = num;
            }

            public static EPriceLevel getPriceLevelFromInt(int len) {
                switch (len) {
                    case 1:
                        return GspaContainer.EPriceLevel.$;
                    case 2:
                        return GspaContainer.EPriceLevel.$$;
                    case 3:
                        return GspaContainer.EPriceLevel.$$$;
                    case 4:
                        return GspaContainer.EPriceLevel.$$$$;
                    default:
                        throw new UnsupportedOperationException();
                }
            }

            public int getNum() {
                return num;
            }
        }
    }
}
