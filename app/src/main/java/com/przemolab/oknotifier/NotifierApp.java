package com.przemolab.oknotifier;

import android.app.Application;

import com.przemolab.oknotifier.services.OpenKattisServiceModule;

import timber.log.Timber;

public class NotifierApp extends Application {

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .openKattisServiceModule(new OpenKattisServiceModule())
                .build();
        appComponent.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
