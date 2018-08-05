package com.przemolab.oknotifier;

import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.services.OpenKattisServiceModule;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = { OpenKattisServiceModule.class })
public interface AppComponent {

    void inject(NotifierApp app);

    void inject(ContestsListFragment contestsListFragment);
}
