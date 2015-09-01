package com.sjgilbert.unanimus;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;
import java.util.Locale;

// TODO: lets get some licensing up in this joint

/**
 * Created by sam on 8/9/15.
 */
public class GroupSettingsPickerActivity extends UnanimusActivityTitle {
    public final static String GSPA = "GspaContainer";
    private final static int radiusMax = 10;
    private final static int radiusStartProgress = radiusMax / 2;
    private final static GspaContainer.EPriceLevel startPriceLevel = GspaContainer.EPriceLevel.$$;

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

        gspaContainer.setDefault();

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

        Button submitButton = (Button) findViewById(R.id.gspa_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                int result;
                if (gspaContainer.isSet()) {
                    intent.putExtra(GSPA, gspaContainer.getAsBundle());
                    result = RESULT_OK;
                } else {
                    result = RESULT_CANCELED;
                }
                setResult(result, intent);
                finish();
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
                "%02d-%02d-%04d",
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
                "%d:%02d",
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

    protected static class GspaContainer extends CreateGroupActivity.ADependencyContainer {
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
        private boolean hasBeenSet = false;

        @Override
        public void setDefault() {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            radius = radiusStartProgress;

            priceLevel = startPriceLevel;

            hasBeenSet = true;
        }

        @Override
        public Bundle getAsBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt(YEAR, year);
            bundle.putInt(MONTH, month);
            bundle.putInt(DAY, day);
            bundle.putInt(HOUR_OF_DAY, hourOfDay);
            bundle.putInt(MINUTE, minute);

            bundle.putInt(RADIUS, radius);

            bundle.putInt(PRICE_LEVEL, priceLevel.getNum());

            try {
                super.getAsBundle();
            } catch (NotSetException e) {
                Log.e("Unanimus", e.getMessage(), e);
                return null;
            }

            return bundle;
        }

        @Override
        public void setFromBundle(Bundle bundle) {
            this.year = bundle.getInt(YEAR);
            this.month = bundle.getInt(MONTH);
            this.day = bundle.getInt(DAY);
            this.hourOfDay = bundle.getInt(HOUR_OF_DAY);
            this.minute = bundle.getInt(MINUTE);
            this.radius = bundle.getInt(RADIUS);
            this.priceLevel = EPriceLevel.getPriceLevelFromInt(bundle.getInt(PRICE_LEVEL));

            this.hasBeenSet = true;
        }

        @Override
        boolean isSet() {
            return hasBeenSet;
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
