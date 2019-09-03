package com.przemolab.oknotifier.utils

import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.data.entries.ContestEntry

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
        fun createContestEntries(count: Int, subscribed: Boolean = false): List<ContestEntry> {
            val contestEntries = ArrayList<ContestEntry>()

            for (i in 1..count) {
                contestEntries.add(createContestEntry(i, subscribed))
            }

            return contestEntries
        }

        @JvmStatic
        fun createContestantEntry(id: Int, contestId: String, name: String = "", problemsSolved: Int = 1, time: Int = 5): ContestantEntry {
            val contestantName = if (name.isEmpty()) String.format("name %s", id) else name
            return ContestantEntry(id = id, name = contestantName, contestId = contestId, problemsSolved = problemsSolved, problemsSubmitted =  2,
                    problemsFailed = 3, problemsNotTried =  4, time =  time)
        }

        @JvmStatic
        fun createContestantEntries(count: Int, contestId: String): List<ContestantEntry> {
            val contestants = ArrayList<ContestantEntry>()

            for (i in 1..count) {
                contestants.add(createContestantEntry(i, contestId))
            }

            return contestants
        }

        @JvmStatic
        fun deleteTablesData(db: AppDatabase) {
            // cleanup data before tests start
            db.contestDao().deleteAll()
            db.contestantDao().deleteAll()
        }
    }
}
