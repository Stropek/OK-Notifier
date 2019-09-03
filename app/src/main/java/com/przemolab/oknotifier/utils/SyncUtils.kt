package com.przemolab.oknotifier.utils

import android.content.Context
import android.support.v7.preference.PreferenceManager

import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.services.OpenKattisJobService

import timber.log.Timber

object SyncUtils {

    private const val OPEN_KATTIS_JOB_SERVICE_TAG = "open-kattis-sync"

    @Synchronized
    fun scheduleSync(context: Context) {
        Timber.d("Sync: scheduling sync")

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val interval = 60 * sharedPreferences.getInt("sync_frequency_sb", context.resources.getInteger(R.integer.default_sync_interval))
        val flextime = 150 // 1/4 of minimum interval value

        val driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)

        var syncJobBuilder: Job.Builder = dispatcher.newJobBuilder()
                .setService(OpenKattisJobService::class.java)
                .setTag(OPEN_KATTIS_JOB_SERVICE_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(interval, interval + flextime))
                .setReplaceCurrent(true)

        if (sharedPreferences.getBoolean("pref_only_on_wifi_switch", context.resources.getBoolean(R.bool.default_sync_wifi_switch))) {
            syncJobBuilder = syncJobBuilder.addConstraint(Constraint.ON_UNMETERED_NETWORK)
        }

        val syncJob = syncJobBuilder.build()
        dispatcher.schedule(syncJob)
    }

    @Synchronized
    fun cancelSync(context: Context) {
        Timber.d("Sync: cancelling sync")

        val driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)
        dispatcher.cancel(OPEN_KATTIS_JOB_SERVICE_TAG)
    }
}
