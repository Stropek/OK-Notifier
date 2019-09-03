package com.przemolab.oknotifier.data.entries

import com.przemolab.oknotifier.utils.DateUtils
import junit.framework.Assert
import org.junit.Test

class ContestantEntryTests {

    @Test
    fun constructor_setsAllFields() {
        // when
        val contest = createContestant()

        // then
        Assert.assertEquals("abc", contest.contestId)
        Assert.assertEquals("name", contest.name)
        Assert.assertEquals("6/10/15 2:00PM", DateUtils.formatDate(contest.createdDate!!, DateUtils.DisplayFormat))
        Assert.assertEquals("6/10/15 3:00PM", DateUtils.formatDate(contest.lastModifiedDate!!, DateUtils.DisplayFormat))
        Assert.assertEquals(1, contest.problemsSolved)
        Assert.assertEquals(2, contest.problemsSubmitted)
        Assert.assertEquals(3, contest.problemsFailed)
        Assert.assertEquals(4, contest.problemsNotTried)
        Assert.assertEquals(5, contest.time)
    }

    @Test
    fun getSharedPreferencesValue_returnsSharedPreferencesInExpectedFormat() {
        // given
        val contest = createContestant()

        // when
        val result = contest.sharedPreferencesValue

        // then
        Assert.assertEquals("abc;1;2;3;4", result)
    }

    private fun createContestant(): ContestantEntry   {
        val id = 1
        val contestId = "abc"
        val name = "name"
        val createdDate = DateUtils.getDate(2015, 6, 10, 14, 0, 0)
        val lastModifiedDate = DateUtils.getDate(2015, 6, 10, 15, 0, 0)
        val solved = 1
        val submitted = 2
        val failed = 3
        val notTried = 4
        val time = 5

        return ContestantEntry(id, contestId, createdDate, lastModifiedDate, name, solved, submitted, failed, notTried, time)
    }
}