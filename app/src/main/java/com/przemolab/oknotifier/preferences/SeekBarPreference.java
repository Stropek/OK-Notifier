package com.przemolab.oknotifier.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import com.przemolab.oknotifier.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeekBarPreference extends Preference {
    private final int intervalMin = 5;
    private int interval = intervalMin;

    @BindView(R.id.value_sb) SeekBar valueBar;
    @BindView(R.id.value_tv) TextView value;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.pref_seekbar);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ButterKnife.bind(this, holder.itemView);

        valueBar.setProgress(interval);
        value.setText(String.format("%sm", interval));

        valueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 10) {
                    value.setText(String.format("%sm", progress));
                    interval = progress;

                    persistInt(interval);

                    notifyChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, interval);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            interval = getPersistedInt(interval);
        } else {
            interval = (int) defaultValue;
            persistInt(interval);
        }
    }
}
