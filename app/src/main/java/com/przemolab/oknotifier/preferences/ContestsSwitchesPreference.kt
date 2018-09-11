package com.przemolab.oknotifier.preferences

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.ToggleButton

import com.przemolab.oknotifier.R

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class ContestsSwitchesPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = R.attr.preferenceStyle, defStyleRes: Int = defStyleAttr) : Preference(context, attrs, defStyleAttr, defStyleRes) {
    private var state = "true;false;false"

    @BindView(R.id.approved_tb) @JvmField
    var approved: ToggleButton? = null
    @BindView(R.id.submitted_tb) @JvmField
    var submitted: ToggleButton? = null
    @BindView(R.id.rejected_tb) @JvmField
    var rejected: ToggleButton? = null

    init {
        layoutResource = R.layout.pref_contests_switches
    }

    @OnClick(R.id.approved_tb, R.id.submitted_tb, R.id.rejected_tb)
    fun onToggleButtonClicked() {
        state = String.format("%s;%s;%s", approved!!.isChecked, submitted!!.isChecked, rejected!!.isChecked)

        persistString(state)

        notifyChanged()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        ButterKnife.bind(this, holder.itemView)

        val states = state.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        approved!!.isChecked = java.lang.Boolean.parseBoolean(states[0])
        submitted!!.isChecked = java.lang.Boolean.parseBoolean(states[1])
        rejected!!.isChecked = java.lang.Boolean.parseBoolean(states[2])
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            state = getPersistedString(state)
        } else {
            state = defaultValue as String
            persistString(state)
        }
    }
}
