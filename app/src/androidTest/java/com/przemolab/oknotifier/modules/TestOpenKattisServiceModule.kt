package com.przemolab.oknotifier.modules

import org.mockito.Mockito

class TestOpenKattisServiceModule : OpenKattisServiceModule() {

    override fun provideOpenKattisService(): OpenKattisService {
        return Mockito.mock(OpenKattisService::class.java)
    }
}
