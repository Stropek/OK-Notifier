package com.przemolab.oknotifier;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component // (modules = { OpenKattisServiceModule })
public interface AppComponent {

    void inject(NotifierApp app);
}
