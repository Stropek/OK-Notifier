package com.przemolab.oknotifier

import com.przemolab.oknotifier.activities.ContestActivitySyncTests
import com.przemolab.oknotifier.activities.ContestActivityTests
import com.przemolab.oknotifier.activities.MainActivitySortTests
import com.przemolab.oknotifier.activities.MainActivitySyncTests
import com.przemolab.oknotifier.activities.MainActivityTests
import com.przemolab.oknotifier.modules.NotifierRepositoryModule
import com.przemolab.oknotifier.modules.OpenKattisServiceModule

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = [(OpenKattisServiceModule::class), (NotifierRepositoryModule::class)])
interface TestAppComponent : AppComponent {

    fun inject(test: MainActivityTests)

    fun inject(test: MainActivitySyncTests)

    fun inject(test: MainActivitySortTests)

    fun inject(test: ContestActivityTests)

    fun inject(test: ContestActivitySyncTests)
}
