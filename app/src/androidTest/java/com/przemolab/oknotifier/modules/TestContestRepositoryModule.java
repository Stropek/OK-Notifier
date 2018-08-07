package com.przemolab.oknotifier.modules;

import android.content.Context;

import org.mockito.Mockito;

public class TestContestRepositoryModule extends ContestRepositoryModule {

    public TestContestRepositoryModule(Context context) {
        super(context);
    }

    @Override
    public ContestRepository provideContestRepository() {
        return Mockito.mock(ContestRepository.class);
    }
}
