package com.przemolab.oknotifier.interfaces

import com.przemolab.oknotifier.models.Contest
import com.przemolab.oknotifier.models.Contestant

interface IOpenKattisService {

    val ongoingContests: List<Contest>?

    fun getContestStandings(contestId: String): List<Contestant>?
}
