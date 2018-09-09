package com.przemolab.oknotifier.interfaces

import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.models.Contest
import com.przemolab.oknotifier.models.Contestant

interface INotifierRepository {

    val subscribed: List<Contest>?

    fun getAll(sortOrder: SortOrder): List<Contest>?

    fun updateContest(contest: Contest)

    fun persistContests(contests: List<Contest>?)

    fun getAllContestants(contestId: String): List<Contestant>?

    fun persistContestants(contestId: String, contestants: List<Contestant>?)
}
