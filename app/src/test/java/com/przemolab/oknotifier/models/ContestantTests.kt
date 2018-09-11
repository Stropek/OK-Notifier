package com.przemolab.oknotifier.models

import org.junit.Test

import junit.framework.Assert.assertEquals

class ContestantTests {

    @Test
    fun constructor_setsAllFields() {
        // when
        val contestant = createContestant()

        // then
        assertEquals(0, contestant.id)
        assertEquals("abc", contestant.contestId)
        assertEquals(1, contestant.problemsSolved)
        assertEquals(2, contestant.problemsSubmitted)
        assertEquals(3, contestant.problemsFailed)
        assertEquals(4, contestant.problemsNotTried)
        assertEquals(5, contestant.time)
        assertEquals("abc;1;2;3;4", contestant.sharedPreferencesValue)
    }

    @Test
    fun setId_setsId() {
        // given
        val contestant = createContestant()

        // when
        contestant.id = 5

        // then
        assertEquals(5, contestant.id)
    }

    @Test
    fun setName_setsName() {
        // given
        val contestant = createContestant()

        // when
        contestant.name = "changed name"

        // then
        assertEquals("changed name", contestant.name)
    }

    @Test
    fun setContestId_setsContestId() {
        // given
        val contestant = createContestant()

        // when
        contestant.contestId = "changed contest id"

        // then
        assertEquals("changed contest id", contestant.contestId)
    }

    @Test
    fun setProblemsSolved_setsProblemsSolved() {
        // given
        val contestant = createContestant()

        // when
        contestant.problemsSolved = 60

        // then
        assertEquals(60, contestant.problemsSolved)
    }

    @Test
    fun setProblemsSubmitted_setsProblemsSubmitted() {
        // given
        val contestant = createContestant()

        // when
        contestant.problemsSubmitted = 40

        // then
        assertEquals(40, contestant.problemsSubmitted)
    }

    @Test
    fun setProblemsFailed_setsProblemsFailed() {
        // given
        val contestant = createContestant()

        // when
        contestant.problemsFailed = 20

        // then
        assertEquals(20, contestant.problemsFailed)
    }

    @Test
    fun setProblemsNotTried_setsProblemsNotTried() {
        // given
        val contestant = createContestant()

        // when
        contestant.problemsNotTried = 30

        // then
        assertEquals(30, contestant.problemsNotTried)
    }

    @Test
    fun setTime_setsTime() {
        // given
        val contestant = createContestant()

        // when
        contestant.time = 10

        // then
        assertEquals(10, contestant.time)
    }

    private fun createContestant(): Contestant {
        val name = "name"
        val contestId = "abc"
        val solved = 1
        val submitted = 2
        val failed = 3
        val notTried = 4
        val time = 5

        return Contestant(name, contestId, solved, submitted, failed, notTried, time)
    }
}
