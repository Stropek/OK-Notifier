package com.przemolab.oknotifier.modules;

import android.content.Context;

import org.mockito.Mockito;

public class TestNotifierRepositoryModule extends NotifierRepositoryModule {

    public TestNotifierRepositoryModule(Context context) {
        super(context);
    }

    @Override
    public NotifierRepository provideNotifierRepository() {
        return Mockito.mock(NotifierRepository.class);
    }
}
