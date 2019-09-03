package com.przemolab.oknotifier

import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors(val diskIO: Executor) {

    companion object {
        private var instance: AppExecutors? = null

        fun getInstance(): AppExecutors? {
            if (instance == null) {
                synchronized(AppExecutors::class.java) {
                    if (instance == null) {
                        Timber.d("Creating new executors instance")
                        instance = AppExecutors(Executors.newSingleThreadExecutor())
                    }
                }
            }
            return instance
        }
    }
}