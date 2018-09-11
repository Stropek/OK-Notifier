package com.przemolab.oknotifier.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.activities.MainActivity
import com.przemolab.oknotifier.utils.SyncUtils

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(activity, MainActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = preferenceScreen.sharedPreferences
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        if (sharedPreferences.getBoolean(Constants.SharedPreferences.NotificationsSwitch, false)) {
            SyncUtils.scheduleSync(activity!!)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Constants.SharedPreferences.NotificationsSwitch && !sharedPreferences.getBoolean(key, false)) {
            SyncUtils.cancelSync(activity!!)
        }
    }
}
