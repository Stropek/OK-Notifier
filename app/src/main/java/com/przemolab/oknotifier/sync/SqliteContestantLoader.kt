package com.przemolab.oknotifier.sync

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

import com.przemolab.oknotifier.fragments.ContestantsListFragment
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.models.Contestant

import timber.log.Timber

class SqliteContestantLoader(context: Context, private val notifierRepository: INotifierRepository, private val openKattisService: IOpenKattisService, private val contestId: String,
                             private val onContestantsListEventListener: ContestantsListFragment.OnContestantsListEventListener) : AsyncTaskLoader<List<Contestant>>(context) {

    private var contestants: List<Contestant>? = null

    override fun loadInBackground(): List<Contestant>? {
        return try {
            Timber.d("Loading contestants from SQLite")
            var contestants = notifierRepository.getAllContestants(contestId)
            if (contestants!!.isEmpty()) {
                Timber.d("No contestants in SQLite. Loading from Open Kattis Service")
                contestants = openKattisService.getContestStandings(contestId)
                notifierRepository.persistContestants(contestId, contestants)
            }
            contestants
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

    }

    override fun onStartLoading() {
        onContestantsListEventListener.onContestantsSyncStarted()
        if (contestants == null) {
            forceLoad()
        } else {
            deliverResult(contestants)
        }
    }

    override fun deliverResult(data: List<Contestant>?) {
        contestants = data
        onContestantsListEventListener.onContestantsSyncFinished(data, false)
        super.deliverResult(data)
    }
}
