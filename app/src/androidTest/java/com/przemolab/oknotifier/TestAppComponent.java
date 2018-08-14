package com.przemolab.oknotifier;

import com.przemolab.oknotifier.activities.ContestActivityTests;
import com.przemolab.oknotifier.activities.MainActivitySortTests;
import com.przemolab.oknotifier.activities.MainActivitySyncTests;
import com.przemolab.oknotifier.activities.MainActivityTests;
import com.przemolab.oknotifier.modules.ContestRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisServiceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { OpenKattisServiceModule.class, ContestRepositoryModule.class } )
public interface TestAppComponent extends AppComponent {

    void inject(MainActivityTests test);

    void inject(MainActivitySyncTests test);

    void inject(MainActivitySortTests test);

    void inject(ContestActivityTests test);
}
