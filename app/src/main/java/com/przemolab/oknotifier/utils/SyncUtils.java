package com.przemolab.oknotifier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.services.OpenKattisJobService;

import timber.log.Timber;

public class SyncUtils {

    private static final String OPEN_KATTIS_JOB_SERVICE_TAG = "open-kattis-sync";

    synchronized public static void scheduleSync(final Context context) {
        Timber.d("SyncTest: scheduling sync");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int interval = 60 * sharedPreferences.getInt("sync_frequency_sb", context.getResources().getInteger(R.integer.default_sync_interval));
        int flextime = 150; // 1/4 of minimum interval value

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job.Builder syncJobBuilder = dispatcher.newJobBuilder()
                .setService(OpenKattisJobService.class)
                .setTag(OPEN_KATTIS_JOB_SERVICE_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(interval, interval + flextime))
                .setReplaceCurrent(true);

        if (sharedPreferences.getBoolean("pref_only_on_wifi_switch", context.getResources().getBoolean(R.bool.default_sync_wifi_switch))) {
            syncJobBuilder = syncJobBuilder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Job syncJob = syncJobBuilder.build();
        dispatcher.schedule(syncJob);
    }

    synchronized public static void cancelSync(final Context context) {
        Timber.d("SyncTest: cancelling sync");

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(OPEN_KATTIS_JOB_SERVICE_TAG);
    }
}
