package com.przemolab.oknotifier.modules

import android.content.Context
import com.przemolab.oknotifier.interfaces.INotifierRepository

import org.mockito.Mockito

class TestNotifierRepositoryModule(context: Context) : NotifierRepositoryModule(context) {

    override fun provideNotifierRepository(): INotifierRepository {
        return Mockito.mock(INotifierRepository::class.java)
    }
}
