package com.przemolab.oknotifier.utils

import junit.framework.Assert

import java.util.concurrent.Callable

internal abstract class PollingCheck(private val _timeout: Long) {

    protected abstract fun check(): Boolean?

    fun run() {
        if (check()!!) {
            return
        }

        var timeout = _timeout
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE)
            } catch (e: InterruptedException) {
                Assert.fail("Notification error, unexpected InterruptedException")
            }

            if (check()!!) {
                return
            }

            timeout -= TIME_SLICE
        }

        Assert.fail("Notification not set, unexpected timeout")
    }

    companion object {

        private val TIME_SLICE = 50L

        @Throws(Exception::class)
        fun check(message: CharSequence, timeout: Long, condition: Callable<Boolean>) {
            var _timeout = timeout
            while (_timeout > 0) {
                if (condition.call()) {
                    return
                }

                Thread.sleep(TIME_SLICE)
                _timeout -= TIME_SLICE
            }

            Assert.fail(message.toString())
        }
    }
}
