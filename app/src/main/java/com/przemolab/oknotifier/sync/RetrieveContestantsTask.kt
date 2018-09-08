package com.przemolab.oknotifier.sync

import android.os.AsyncTask

import com.przemolab.oknotifier.fragments.ContestantsListFragment
import com.przemolab.oknotifier.models.Contestant
import com.przemolab.oknotifier.modules.NotifierRepository
import com.przemolab.oknotifier.modules.OpenKattisService

import timber.log.Timber

class RetrieveContestantsTask(private val openKattisService: OpenKattisService, private val notifierRepository: NotifierRepository, private val contestId: String,
                              private val onContestantsListEventListener: ContestantsListFragment.OnContestantsListEventListener) : AsyncTask<Void, Void, List<Contestant>>() {

    override fun onPreExecute() {
        onContestantsListEventListener.onContestantsSyncStarted()
    }

    override fun doInBackground(vararg voids: Void): List<Contestant>? {
        try {
            val contestants = openKattisService.getContestStandings(contestId)
            notifierRepository.persistContestants(contestId, contestants)
            return contestants
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return null
    }

    override fun onPostExecute(contestants: List<Contestant>) {
        onContestantsListEventListener.onContestantsSyncFinished(contestants, true)
    }
}
