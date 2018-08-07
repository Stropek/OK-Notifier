package com.przemolab.oknotifier;

import com.przemolab.oknotifier.activities.MainActivityTests;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.modules.ContestRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisServiceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ContestRepositoryModule.class } )
public interface TestAppComponent extends AppComponent {

    void inject(MainActivityTests test);
}
