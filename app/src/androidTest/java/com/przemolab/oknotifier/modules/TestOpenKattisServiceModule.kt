package com.przemolab.oknotifier.modules

import com.przemolab.oknotifier.interfaces.IOpenKattisService
import org.mockito.Mockito

class TestOpenKattisServiceModule : OpenKattisServiceModule() {

    override fun provideOpenKattisService(): IOpenKattisService {
        return Mockito.mock(IOpenKattisService::class.java)
    }
}
