package com.przemolab.oknotifier.modules

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
open class OpenKattisServiceModule {

    @Provides
    @Singleton
    open fun provideOpenKattisService(): OpenKattisService {
        return OpenKattisService()
    }
}
