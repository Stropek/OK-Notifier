package com.przemolab.oknotifier.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContestRepositoryModule {

    private Context context;

    public ContestRepositoryModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public ContestRepository provideContestRepository() {
        return new ContestRepository(context);
    }
}
