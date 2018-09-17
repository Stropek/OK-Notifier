package com.przemolab.oknotifier.modules

import android.content.ContentValues
import android.content.Context

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.models.Contest
import com.przemolab.oknotifier.models.Contestant

import java.util.ArrayList
import java.util.Arrays

import timber.log.Timber

class NotifierRepository(private val context: Context) : INotifierRepository {

    override val subscribed: List<Contest>?
        get() {
            try {
                val subscribedContests = ArrayList<Contest>()
                val contestsUri = NotifierContract.ContestEntry.CONTENT_URI
                val cursor = context.contentResolver
                        .query(contestsUri, null, NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED + "=1", null, null)

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        subscribedContests.add(Contest.getFromCursor(cursor))
                    }

                    cursor.close()
                }

                return subscribedContests
            } catch (ex: Exception) {
                Timber.e(ex)
                return null
            }
        }

    override fun getAll(sortOrder: SortOrder): List<Contest>? {
        try {
            val contests = ArrayList<Contest>()
            val contestsUri = NotifierContract.ContestEntry.CONTENT_URI
            val cursor = context.contentResolver
                    .query(contestsUri, null, null, null, getSortOrder(sortOrder))

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contests.add(Contest.getFromCursor(cursor))
                }

                cursor.close()
            }

            return contests
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }

    }

    override fun persistContests(contests: List<Contest>?) {
        try {
            val persistedContests = getAll(SortOrder.SubscribedFirst)
            val updatedContests = ArrayList<Contest>()

            for (contest in contests!!) {
                var exists = false

                for (persistedContest in persistedContests!!) {
                    if (persistedContest.contestId == contest.contestId) {
                        contest.isSubscribed = persistedContest.isSubscribed
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

            val contestsToDelete = ArrayList<Contest>()
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

    override fun updateContest(contest: Contest) {
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        Timber.d("Updating contest: %s [%s]", contest.name, contest.contestId)
        context.contentResolver.update(uri, contest.toContentValues(),
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                arrayOf(contest.contestId))
    }

    override fun getAllContestants(contestId: String): List<Contestant>? {
        try {
            val contestants = ArrayList<Contestant>()
            val contestantsUri = NotifierContract.ContestantEntry.CONTENT_URI.buildUpon()
                    .appendPath("byContestId")
                    .appendPath(contestId)
                    .build()
            val sortOrder = NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED + " DESC, " + NotifierContract.ContestantEntry.COLUMN_TIME + " DESC"
            val cursor = context.contentResolver
                    .query(contestantsUri, null, null, null, sortOrder)

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contestants.add(Contestant.getFromCursor(cursor))
                }

                cursor.close()
            }

            return contestants
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }

    }

    override fun persistContestants(contestId: String, contestants: List<Contestant>?) {
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
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        Timber.d("Updating # of contestants in contest: %s [%s]", contestId, numberOfContestants)

        val contest = context.contentResolver.query(uri, null,
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                arrayOf(contestId), null)
        contest.moveToFirst()
        val contentValues = ContentValues()
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, numberOfContestants)
        contest.close()

        context.contentResolver.update(uri, contentValues,
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                arrayOf(contestId))
    }

    private fun createContestant(contestant: Contestant) {
        val uri = NotifierContract.ContestantEntry.CONTENT_URI

        Timber.d("Creating contestant: %s [%s]", contestant.name, contestant.contestId)
        context.contentResolver.insert(uri, contestant.toContentValues())
    }

    private fun updateContestant(contestant: Contestant) {
        val uri = NotifierContract.ContestantEntry.CONTENT_URI

        Timber.d("Updating contestant: %s [%s]", contestant.name, contestant.contestId)
        context.contentResolver.update(uri, contestant.toContentValues(),
                NotifierContract.ContestantEntry._ID + "=?",
                arrayOf(contestant.id.toString()))
    }

    private fun createContest(contest: Contest) {
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        Timber.d("Creating contest: %s [%s]", contest.name, contest.contestId)
        context.contentResolver.insert(uri, contest.toContentValues())
    }

    private fun deleteContests(contestsToDelete: List<Contest>) {
        val deleteUri = NotifierContract.ContestEntry.CONTENT_URI
                .buildUpon().appendPath("byContestIds").build()

        val contestsIds = arrayOfNulls<String>(contestsToDelete.size)
        for (i in contestsToDelete.indices) {
            contestsIds[i] = contestsToDelete[i].contestId
        }

        Timber.d("Deleting contests: %s", Arrays.toString(contestsIds))
        context.contentResolver.delete(deleteUri, null, contestsIds)
    }

    private fun getSortOrder(sortOrder: SortOrder): String {
        var orderBy = NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED + " DESC"
        when (sortOrder) {
            SortOrder.ByName -> orderBy = NotifierContract.ContestEntry.COLUMN_NAME
            SortOrder.ByStartDate -> orderBy = NotifierContract.ContestEntry.COLUMN_START_DATE
            SortOrder.ByNumberOfProblems -> orderBy = NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS + " DESC"
            SortOrder.ByNumberOfContestants -> orderBy = NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS + " DESC"
        }

        return orderBy
    }
}
