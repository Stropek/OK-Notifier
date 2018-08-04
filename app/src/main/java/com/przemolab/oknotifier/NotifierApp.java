package com.przemolab.oknotifier;

import android.app.Application;

public class NotifierApp extends Application {

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .build();

        appComponent.inject(this);
    }
}
