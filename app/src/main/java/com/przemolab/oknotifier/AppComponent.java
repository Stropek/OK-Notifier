package com.przemolab.oknotifier;

import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.modules.NotifierRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisServiceModule;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = { OpenKattisServiceModule.class, NotifierRepositoryModule.class})
public interface AppComponent {

    void inject(NotifierApp app);

    void inject(ContestsListFragment contestsListFragment);

    void inject(ContestantsListFragment contestantsListFragment);
}
