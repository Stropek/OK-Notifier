package com.przemolab.oknotifier.data.entries

import com.przemolab.oknotifier.utils.DateUtils
import junit.framework.Assert
import org.junit.Test

class ContestEntryTests {

    @Test
    fun constructor_setsAllFields() {
        // when
        val contest = createContest()

        // then
        Assert.assertEquals("abc", contest.contestId)
        Assert.assertEquals("name", contest.name)
        Assert.assertEquals("6/10/15 4:00PM", contest.startDateFormatted)
        Assert.assertEquals("6/15/15 5:00PM", contest.endDateFormatted)
        Assert.assertEquals(5, contest.numberOfContestants)
        Assert.assertEquals(10, contest.numberOfProblems)
    }

    @Test
    fun getStartDateFormatted_returnsStartDateInExpectedFormat() {
        // given
        val contest = createContest()

        // when
        val result = contest.startDateFormatted

        // then
        Assert.assertEquals("6/10/15 4:00PM", result)
    }

    @Test
    fun getEndDateFormatted_returnsEndDateInExpectedFormat() {
        // given
        val contest = createContest()

        // when
        val result = contest.endDateFormatted

        // then
        Assert.assertEquals("6/15/15 5:00PM", result)
    }

    private fun createContest(): ContestEntry {
        val id = 1
        val contestId = "abc"
        val name = "name"
        val createdDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val lastModifiedDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val endDate = DateUtils.getDate(2015, 6, 15, 17, 0, 0)
        val numberOfContestants = 5
        val numberOfProblems = 10

        return ContestEntry(id, contestId, createdDate, lastModifiedDate, name, startDate, endDate, numberOfContestants, numberOfProblems)
    }
}