package com.przemolab.oknotifier.sync

import android.os.AsyncTask

import com.przemolab.oknotifier.fragments.ContestsListFragment
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.models.Contest

import timber.log.Timber

class RetrieveContestsTask(private val openKattisService: IOpenKattisService, private val notifierRepository: INotifierRepository,
                           private val onContestsListEventsListener: ContestsListFragment.OnContestsListEventsListener) : AsyncTask<Void, Void, List<Contest>>() {

    override fun onPreExecute() {
        onContestsListEventsListener.onContestSyncStarted()
    }

    override fun doInBackground(vararg voids: Void): List<Contest>? {
        try {
            val contests = openKattisService.ongoingContests
            notifierRepository.persistContests(contests)
            return contests
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return null
    }

    override fun onPostExecute(contests: List<Contest>) {
        onContestsListEventsListener.onContestSyncFinished(contests)
    }
}
