package com.przemolab.oknotifier.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NotifierRepositoryModule {

    private Context context;

    public NotifierRepositoryModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public NotifierRepository provideNotifierRepository() {
        return new NotifierRepository(context);
    }
}
