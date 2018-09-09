package com.przemolab.oknotifier.modules

import android.content.Context
import com.przemolab.oknotifier.interfaces.INotifierRepository

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
open class NotifierRepositoryModule(private val context: Context) {

    @Provides
    @Singleton
    open fun provideNotifierRepository(): INotifierRepository {
        return NotifierRepository(context)
    }
}
