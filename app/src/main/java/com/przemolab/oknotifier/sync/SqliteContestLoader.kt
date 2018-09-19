package com.przemolab.oknotifier.sync

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.przemolab.oknotifier.data.ContestEntry

import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.interfaces.INotifierRepository

import timber.log.Timber

class SqliteContestLoader(context: Context,
                          private val notifierRepository: INotifierRepository,
                          private val sortOrder: SortOrder) : AsyncTaskLoader<List<ContestEntry>>(context) {

    private var contests: List<ContestEntry>? = null

    override fun loadInBackground(): List<ContestEntry>? {
        return try {
            Timber.d("Loading contests from SQLite")
            notifierRepository.getAll(sortOrder)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    override fun onStartLoading() {
        if (contests == null) {
            forceLoad()
        } else {
            deliverResult(contests)
        }
    }

    override fun deliverResult(data: List<ContestEntry>?) {
        contests = data
        super.deliverResult(data)
    }
}
