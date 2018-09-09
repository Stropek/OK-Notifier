package com.przemolab.oknotifier.modules

import com.przemolab.oknotifier.interfaces.IOpenKattisService
import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
open class OpenKattisServiceModule {

    @Provides
    @Singleton
    open fun provideOpenKattisService(): IOpenKattisService {
        return OpenKattisService()
    }
}
