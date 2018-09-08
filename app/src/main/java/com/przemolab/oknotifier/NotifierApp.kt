package com.przemolab.oknotifier

import android.app.Application

import com.przemolab.oknotifier.modules.NotifierRepositoryModule
import com.przemolab.oknotifier.modules.OpenKattisServiceModule

import timber.log.Timber

class NotifierApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .openKattisServiceModule(OpenKattisServiceModule())
                .notifierRepositoryModule(NotifierRepositoryModule(this))
                .build()
        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
