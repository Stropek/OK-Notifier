package com.przemolab.oknotifier.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.data.entries.ContestEntry

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.data.NotifierDbHelper
import com.przemolab.oknotifier.data.entries.ContestantEntry

import java.util.ArrayList

class DataHelper {
    companion object {

        @JvmStatic
        fun createContestEntry(id: Int, subscribed: Boolean = false, contestId: String = ""): ContestEntry {
            val contestEntryId = if (contestId.isEmpty()) String.format("id %s", id) else contestId
            val name = String.format("id %s", id)

            val contestEntry = ContestEntry(contestId = contestEntryId, name = name)
            contestEntry.id = id
            contestEntry.startDate = DateUtils.getDate(2000 + id, id, id, id, id, 0)
            contestEntry.endDate = DateUtils.getDate(2001 + id, id + 1, id + 1, id + 1, id + 1, 0)
            contestEntry.subscribed = subscribed
            return contestEntry
        }

        @JvmStatic
        fun createContestEntries(count: Int): List<ContestEntry> {
            val contestEntries = ArrayList<ContestEntry>()

            for (i in 1..count) {
                contestEntries.add(createContestEntry(i))
            }

            return contestEntries
        }

        @JvmStatic
        fun createContestant(id: Int, contestId: String, name: String = "", problemsSolved: Int = 1, time: Int = 5): ContestantEntry {
            val contestantName = if (name.isEmpty()) String.format("name %s", id) else name
            return ContestantEntry(id = id, name = contestantName, contestId = contestId, problemsSolved = problemsSolved, problemsSubmitted =  2,
                    problemsFailed = 3, problemsNotTried =  4, time =  time)
        }

        @JvmStatic
        fun createContestants(count: Int, contestId: String): List<ContestantEntry> {
            val contestants = ArrayList<ContestantEntry>()

            for (i in 1..count) {
                contestants.add(createContestant(i, contestId))
            }

            return contestants
        }

        @JvmOverloads
        @JvmStatic
        fun insertContest(contentResolver: ContentResolver, uri: Uri, name: String, id: String,
                          startDate: String, endDate: String, numOfContestants: Int, numOfProblems: Int,
                          isSubscribed: Boolean = false): Uri? {

            val contentValues = ContentValues()

            contentValues.put(NotifierContract.ContestEntry.COLUMN_NAME, name)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_CONTEST_ID, id)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_START_DATE, startDate)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_END_DATE, endDate)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, numOfContestants)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, numOfProblems)
            contentValues.put(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED, isSubscribed)

            return contentResolver.insert(uri, contentValues)
        }

        @JvmOverloads
        @JvmStatic
        fun insertContestant(contentResolver: ContentResolver, uri: Uri, contestId: String, name: String = "john doe"): Uri? {
            val contentValues = ContentValues()

            contentValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, name)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, contestId)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, 1)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, 2)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, 3)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, 4)
            contentValues.put(NotifierContract.ContestantEntry.COLUMN_TIME, 5)

            return contentResolver.insert(uri, contentValues)
        }

        @JvmStatic
        fun setObservedUriOnContentResolver(contentResolver: ContentResolver, uri: Uri, contentObserver: ContentObserver) {
            contentResolver.registerContentObserver(
                    /* URI that we would like to observe changes to */
                    uri,
                    /* Whether or not to notify us if descendants of this URI change */
                    true,
                    /* The observer to register (that will receive notifyChange callbacks) */
                    contentObserver)
        }

        @JvmStatic
        fun deleteTablesData(db: AppDatabase) {
            // cleanup data before tests start
            db.contestDao().deleteAll()
        }

        @JvmStatic
        fun deleteTablesDataOld(context: Context) {
            val dbHelper = NotifierDbHelper(context)
            val database = dbHelper.writableDatabase

            // cleanup data before tests start
            database.delete(NotifierContract.ContestEntry.TABLE_NAME, null, null)
            database.delete(NotifierContract.ContestantEntry.TABLE_NAME, null, null)
        }
    }
}
