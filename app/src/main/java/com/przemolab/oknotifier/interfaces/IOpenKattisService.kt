package com.przemolab.oknotifier.interfaces

import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.data.entries.ContestantEntry

interface IOpenKattisService {

    val ongoingContests: List<ContestEntry>?

    fun getContestStandings(contestId: String): List<ContestantEntry>?
}
