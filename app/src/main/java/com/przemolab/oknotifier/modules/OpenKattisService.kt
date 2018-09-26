package com.przemolab.oknotifier.modules

import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.models.Contestant
import com.przemolab.oknotifier.utils.DateUtils

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import java.util.ArrayList

import timber.log.Timber

class OpenKattisService : IOpenKattisService {

    private val _baseKattisUrl = "https://open.kattis.com"

    override val ongoingContests: List<ContestEntry>?
        get() {
            try {
                val contests = ArrayList<ContestEntry>()

                val url = String.format("%s/contests", _baseKattisUrl)
                val contestsPageDocument = Jsoup.connect(url).timeout(0).get()
                val elements = contestsPageDocument.select(".main-content table:first-of-type tbody tr")

                for (row in elements) {
                    val contest = getContest(row)
                    if (contest != null) {
                        contests.add(contest)
                    }
                }

                return contests
            } catch (ex: Exception) {
                Timber.e(ex)
                return null
            }
        }

    override fun getContestStandings(contestId: String): List<Contestant>? {
        try {
            val contestants = ArrayList<Contestant>()

            val url = String.format("%s/contests/%s", _baseKattisUrl, contestId)
            val contestsPageDocument = Jsoup.connect(url).timeout(0).get()
            val elements = contestsPageDocument.select("#standings tbody tr")

            for (row in elements) {
                val contestant = getContestant(row, contestId)
                if (contestant != null) {
                    contestants.add(contestant)
                }
            }

            return contestants
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }

    }

    private fun getContest(row: Element): ContestEntry? {
        try {
            val cells = row.select("td")

            val name = cells[0].select("a").text()
            val contestUrl = String.format("%s%s", _baseKattisUrl, cells[0].select("a").attr("href"))
            val parts = contestUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val contestId = parts[parts.size - 1]

            val startDateText = cells[3].text().replace(" CEST", "")

            var dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
            if (startDateText.length == 8) {
                dateTimeFormat = "HH:mm:ss"
            }
            val startDate = DateUtils.getDate(startDateText, dateTimeFormat)

            val lengthText = cells[2].text()
            val endDate = DateUtils.addTimeToDate(startDate, lengthText)

            val standingsPageDocument = Jsoup.connect(contestUrl).timeout(0).get()
            val numberOfContestants = standingsPageDocument.select("#standings tbody tr").size - 4
            val numberOfProblems = standingsPageDocument.select("#standings thead th.problemcolheader-standings").size

            return ContestEntry(contestId = contestId, name = name, startDate = startDate, endDate = endDate,
                    numberOfContestants = numberOfContestants, numberOfProblems = numberOfProblems)
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }
    }

    private fun getContestant(row: Element, contestId: String): Contestant? {
        try {
            val cells = row.select("td")

            val rank = cells[0].select(".rank").text()

            if (!rank.isEmpty()) {

                var name = cells[1].select("a").text()
                if (name.isEmpty()) {
                    name = cells[1].select("div").text()
                }
                if (name.isEmpty()) {
                    name = cells[1].select("em").text()
                }

                val time = Integer.valueOf(cells[3].text())

                var solved = 0
                var submitted = 0
                var failed = 0
                var notTried = 0
                for (cell in cells) {
                    if (cell.hasClass("solved")) {
                        solved++
                    } else if (cell.hasClass("pending")) {
                        submitted++
                    } else if (cell.hasClass("attempted")) {
                        failed++
                    } else if (!cell.hasText()) {
                        notTried++
                    }
                }

                return Contestant(name, contestId, solved, submitted, failed, notTried, time)
            }

            return null
        } catch (ex: Exception) {
            Timber.e(ex)
            return null
        }

    }
}