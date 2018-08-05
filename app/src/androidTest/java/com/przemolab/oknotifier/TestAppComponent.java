package com.przemolab.oknotifier;

import com.przemolab.oknotifier.activities.MainActivityTests;
import com.przemolab.oknotifier.services.OpenKattisService;
import com.przemolab.oknotifier.services.OpenKattisServiceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { OpenKattisServiceModule.class } )
public interface TestAppComponent extends AppComponent {

    void inject(MainActivityTests test);
}
