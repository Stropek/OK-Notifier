package com.przemolab.oknotifier.preferences;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.przemolab.oknotifier.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContestsSwitchesPreference extends Preference {
    private String state = "true;false;false";

    @BindView(R.id.approved_tb) ToggleButton approved;
    @BindView(R.id.submitted_tb) ToggleButton submitted;
    @BindView(R.id.rejected_tb) ToggleButton rejected;

    public ContestsSwitchesPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }
    public ContestsSwitchesPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public ContestsSwitchesPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.pref_contests_switches);
    }

    @OnClick({R.id.approved_tb, R.id.submitted_tb, R.id.rejected_tb})
    public void onToggleButtonClicked() {
        state = String.format("%s;%s;%s", approved.isChecked(), submitted.isChecked(), rejected.isChecked());

        persistString(state);

        notifyChanged();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ButterKnife.bind(this, holder.itemView);

        String[] states = state.split(";");
        approved.setChecked(Boolean.parseBoolean(states[0]));
        submitted.setChecked(Boolean.parseBoolean(states[1]));
        rejected.setChecked(Boolean.parseBoolean(states[2]));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            state = getPersistedString(state);
        } else {
            state = (String) defaultValue;
            persistString(state);
        }
    }
}
