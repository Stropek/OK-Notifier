package com.przemolab.oknotifier.utils

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread

class TestContentObserver private constructor(private val _handlerThread: HandlerThread) : ContentObserver(Handler(_handlerThread.looper)) {
    private var _contentChanged: Boolean? = false

    override fun onChange(selfChange: Boolean) {
        onChange(selfChange, null)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        _contentChanged = true
    }

    fun waitForNotificationOrFail() {
        object : PollingCheck(5000L) {
            public override fun check(): Boolean? {
                return _contentChanged
            }
        }.run()

        _handlerThread.quit()
    }

    companion object {

        @JvmStatic
        val testContentObserver: TestContentObserver
            get() {
                val ht = HandlerThread("ContentObserverThread")
                ht.start()
                return TestContentObserver(ht)
            }
    }
}

