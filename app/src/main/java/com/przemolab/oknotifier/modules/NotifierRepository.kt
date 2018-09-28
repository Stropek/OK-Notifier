package com.przemolab.oknotifier.modules

import android.content.Context
import com.przemolab.oknotifier.AppExecutors
import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.data.entries.ContestEntry

import com.przemolab.oknotifier.data.entries.ContestantEntry
import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.interfaces.INotifierRepository

import java.util.ArrayList

import timber.log.Timber

class NotifierRepository(context: Context) : INotifierRepository {

    val db = AppDatabase.getInstance(context)!!

    override val subscribed: List<ContestEntry>?
        get() {
            return try {
                db.contestDao().getSubscribed()
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

    override fun getAllContests(sortOrder: SortOrder): List<ContestEntry>? {
        return try {
            when (sortOrder) {
                SortOrder.SubscribedFirst -> db.contestDao().getAll().sortedByDescending { it -> it.subscribed }
                SortOrder.ByNumberOfProblems -> db.contestDao().getAll().sortedByDescending { it -> it.numberOfProblems }
                SortOrder.ByNumberOfContestants -> db.contestDao().getAll().sortedByDescending { it -> it.numberOfContestants }
                SortOrder.ByStartDate -> db.contestDao().getAll().sortedBy { it -> it.startDate }
                else ->
                    db.contestDao().getAll().sortedBy { it -> it.name }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    override fun persistContests(contestEntries: List<ContestEntry>?) {
        try {
            val persistedContests = getAllContests(SortOrder.SubscribedFirst)
            val updatedContests = ArrayList<ContestEntry>()

            for (contest in contestEntries!!) {
                var exists = false

                for (persistedContest in persistedContests!!) {
                    if (persistedContest.contestId == contest.contestId) {
                        contest.subscribed = persistedContest.subscribed
                        exists = true
                        break
                    }
                }

                if (exists) {
                    updateContest(contest)
                    updatedContests.add(contest)
                } else {
                    createContest(contest)
                }
            }

            val contestsToDelete = ArrayList<ContestEntry>()
            for (persistedContest in persistedContests!!) {
                var toDelete = true

                for (updatedContest in updatedContests) {
                    if (persistedContest.contestId == updatedContest.contestId) {
                        toDelete = false
                        break
                    }
                }

                if (toDelete) {
                    contestsToDelete.add(persistedContest)
                }
            }

            if (contestsToDelete.size > 0) {
                deleteContests(contestsToDelete)
            }

        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    override fun updateContest(contestEntry: ContestEntry) {
        Timber.d("Updating contest: %s [%s]", contestEntry.name, contestEntry.contestId)
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestDao().update(contestEntry)
            }
        }
    }

    override fun getAllContestants(contestId: String): List<ContestantEntry>? {
        return try {
            db.contestantDao().getByContestId(contestId)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    override fun persistContestants(contestId: String, contestants: List<ContestantEntry>?) {
        Timber.d("Persisting contestants")
        try {
            val persistedContestants = getAllContestants(contestId)!!

            for (contestant in contestants!!) {
                var exists = false
                val contestantId = String.format("%s - %s", contestant.name, contestant.contestId)

                for (persistedContestant in persistedContestants) {
                    val persistedContestantId = String.format("%s - %s", persistedContestant.name, persistedContestant.contestId)
                    if (persistedContestantId == contestantId) {
                        contestant.id = persistedContestant.id
                        exists = true
                        break
                    }
                }

                if (exists) {
                    updateContestant(contestant)
                } else {
                    createContestant(contestant)
                }
            }

            if (contestants.size > persistedContestants.size) {
                updateNumberOfContestants(contestId, contestants.size)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun updateNumberOfContestants(contestId: String, numberOfContestants: Int) {
        Timber.d("Updating # of contestants in contest: %s [%s]", contestId, numberOfContestants)
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestDao().updateNumberOfContestants(contestId, numberOfContestants)
            }
        }
    }

    private fun createContestant(contestantEntry: ContestantEntry) {
        Timber.d("Creating contestant: %s [%s]", contestantEntry.name, contestantEntry.contestId)
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestantDao().insert(contestantEntry)
            }
        }
    }

    private fun updateContestant(contestantEntry: ContestantEntry) {
        Timber.d("Updating contestant: %s [%s]", contestantEntry.name, contestantEntry.contestId)
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestantDao().update(contestantEntry)
            }
        }
    }

    private fun createContest(contestEntry: ContestEntry) {
        Timber.d("Creating contest: %s [%s]", contestEntry.name, contestEntry.contestId)
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestDao().insert(contestEntry)
            }
        }
    }

    private fun deleteContests(contestsToDelete: List<ContestEntry>) {
        Timber.d("Deleting contests: %s", contestsToDelete.map { it -> it.contestId })
        AppExecutors.getInstance()?.diskIO?.execute {
            kotlin.run {
                db.contestDao().deleteMany(contestsToDelete)
            }
        }
    }
}
