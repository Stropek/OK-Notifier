package com.przemolab.oknotifier.models

import com.przemolab.oknotifier.utils.DateUtils

import org.junit.Test

import java.util.Date

import junit.framework.Assert.assertEquals

class ContestTests {

    @Test
    fun constructor_setsAllFields() {
        // when
        val contest = createContest()

        // then
        assertEquals("abc", contest.contestId)
        assertEquals("name", contest.name)
        assertEquals("6/10/15 4:00PM", contest.startDateFormatted)
        assertEquals("6/15/15 5:00PM", contest.endDateFormatted)
        assertEquals(5, contest.numberOfContestants)
        assertEquals(10, contest.numberOfProblems)
    }

    @Test
    fun getStartDateFormatted_returnsStartDateInExpectedFormat() {
        // given
        val contest = createContest()

        // when
        val result = contest.startDateFormatted

        // then
        assertEquals("6/10/15 4:00PM", result)
    }

    @Test
    fun getEndDateFormatted_returnsEndDateInExpectedFormat() {
        // given
        val contest = createContest()

        // when
        val result = contest.endDateFormatted

        // then
        assertEquals("6/15/15 5:00PM", result)
    }

    @Test
    fun setId_setsId() {
        // given
        val contest = createContest()

        // when
        contest.contestId = "changed id"

        // then
        assertEquals("changed id", contest.contestId)
    }

    @Test
    fun setName_setsName() {
        // given
        val contest = createContest()

        // when
        contest.name = "changed name"

        // then
        assertEquals("changed name", contest.name)
    }

    @Test
    fun setStartDate_setsStartDate() {
        // given
        val contest = createContest()
        val newDate = DateUtils.getDate(2000, 1, 1, 10, 30, 0)

        // when
        contest.startDate = newDate

        // then
        assertEquals(newDate, contest.startDate)
    }

    @Test
    fun setEndDate_setsEndDate() {
        // given
        val contest = createContest()
        val newDate = DateUtils.getDate(2000, 1, 1, 10, 30, 0)

        // when
        contest.endDate = newDate

        // then
        assertEquals(newDate, contest.endDate)
    }

    @Test
    fun setNumberOfContestants_setsNumberOfContestants() {
        // given
        val contest = createContest()

        // when
        contest.numberOfContestants = 100

        // then
        assertEquals(100, contest.numberOfContestants)
    }

    @Test
    fun setNumberOfProblems_setsNumberOfProblems() {
        // given
        val contest = createContest()

        // when
        contest.numberOfProblems = 100

        // then
        assertEquals(100, contest.numberOfProblems)
    }

    @Test
    fun setSubscribed_setsSubscribed() {
        // given
        val contest = createContest()

        // when
        contest.isSubscribed = true

        // then
        assertEquals(true, contest.isSubscribed)
    }

    private fun createContest(): Contest {
        val id = "abc"
        val name = "name"
        val startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val endDate = DateUtils.getDate(2015, 6, 15, 17, 0, 0)
        val numberOfContestants = 5
        val numberOfProblems = 10

        return Contest(id, name, startDate, endDate, numberOfContestants, numberOfProblems)
    }
}