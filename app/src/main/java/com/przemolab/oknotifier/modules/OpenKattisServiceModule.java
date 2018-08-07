package com.przemolab.oknotifier.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class OpenKattisServiceModule {

    @Provides
    @Singleton
    public OpenKattisService provideOpenKattisService() {
        return new OpenKattisService();
    }
}
