package com.przemolab.oknotifier.preferences

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.TextView

import com.przemolab.oknotifier.R

import butterknife.BindView
import butterknife.ButterKnife

class SeekBarPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = R.attr.preferenceStyle, defStyleRes: Int = defStyleAttr) : Preference(context, attrs, defStyleAttr, defStyleRes) {
    private val intervalMin = 5
    private var interval = intervalMin

    @BindView(R.id.value_sb) @JvmField
    var valueBar: SeekBar? = null
    @BindView(R.id.value_tv) @JvmField
    var value: TextView? = null

    init {
        layoutResource = R.layout.pref_seekbar
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        ButterKnife.bind(this, holder.itemView)

        valueBar!!.progress = interval
        value!!.text = String.format("%sm", interval)

        valueBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress >= 10) {
                    value!!.text = String.format("%sm", progress)
                    interval = progress

                    persistInt(interval)

                    notifyChanged()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a!!.getInt(index, interval)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            interval = getPersistedInt(interval)
        } else {
            interval = defaultValue as Int
            persistInt(interval)
        }
    }
}
