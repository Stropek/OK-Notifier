package com.przemolab.oknotifier.modules

import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
open class NotifierRepositoryModule(private val context: Context) {

    @Provides
    @Singleton
    open fun provideNotifierRepository(): NotifierRepository {
        return NotifierRepository(context)
    }
}
