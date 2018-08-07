package com.przemolab.oknotifier.modules;

import org.mockito.Mockito;

public class TestOpenKattisServiceModule extends OpenKattisServiceModule {

    @Override
    public OpenKattisService provideOpenKattisService() {
        return Mockito.mock(OpenKattisService.class);
    }
}
