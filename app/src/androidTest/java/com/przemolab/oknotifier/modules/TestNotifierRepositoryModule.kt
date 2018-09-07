package com.przemolab.oknotifier.modules

import android.content.Context

import org.mockito.Mockito

class TestNotifierRepositoryModule(context: Context) : NotifierRepositoryModule(context) {

    override fun provideNotifierRepository(): NotifierRepository {
        return Mockito.mock(NotifierRepository::class.java)
    }
}
