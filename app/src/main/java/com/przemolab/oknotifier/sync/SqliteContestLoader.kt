package com.przemolab.oknotifier.sync

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.models.Contest
import com.przemolab.oknotifier.modules.NotifierRepository

import timber.log.Timber

class SqliteContestLoader(context: Context, private val notifierRepository: NotifierRepository, private val sortOrder: SortOrder) : AsyncTaskLoader<List<Contest>>(context) {

    private var contests: List<Contest>? = null

    override fun loadInBackground(): List<Contest>? {
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

    override fun deliverResult(data: List<Contest>?) {
        contests = data
        super.deliverResult(data)
    }
}
