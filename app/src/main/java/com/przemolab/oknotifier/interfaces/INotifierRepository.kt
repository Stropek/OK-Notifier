package com.przemolab.oknotifier.interfaces

import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.data.entries.ContestantEntry
import com.przemolab.oknotifier.enums.SortOrder

interface INotifierRepository {

    val subscribed: List<ContestEntry>?

    fun getAllContests(sortOrder: SortOrder = SortOrder.SubscribedFirst): List<ContestEntry>?

    fun updateContest(contestEntry: ContestEntry)

    fun persistContests(contestEntries: List<ContestEntry>?)

    fun getAllContestants(contestId: String): List<ContestantEntry>?

    fun persistContestants(contestId: String, contestants: List<ContestantEntry>?)
}
