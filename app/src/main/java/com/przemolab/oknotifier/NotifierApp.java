package com.przemolab.oknotifier;

import android.app.Application;

import com.przemolab.oknotifier.modules.ContestRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisServiceModule;

import timber.log.Timber;

public class NotifierApp extends Application {

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .contestRepositoryModule(new ContestRepositoryModule(this))
                .build();
        appComponent.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
