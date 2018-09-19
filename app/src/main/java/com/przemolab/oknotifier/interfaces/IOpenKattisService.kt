package com.przemolab.oknotifier.interfaces

import com.przemolab.oknotifier.data.ContestEntry
import com.przemolab.oknotifier.models.Contestant

interface IOpenKattisService {

    val ongoingContests: List<ContestEntry>?

    fun getContestStandings(contestId: String): List<Contestant>?
}
