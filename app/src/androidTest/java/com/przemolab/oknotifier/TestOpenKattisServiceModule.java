package com.przemolab.oknotifier;

import com.przemolab.oknotifier.services.OpenKattisService;
import com.przemolab.oknotifier.services.OpenKattisServiceModule;

import org.mockito.Mockito;

public class TestOpenKattisServiceModule extends OpenKattisServiceModule {

    @Override
    public OpenKattisService provideOpenKattisService() {
        return Mockito.mock(OpenKattisService.class);
    }
}
