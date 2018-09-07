package com.przemolab.oknotifier.modules

import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import junit.framework.Assert.assertNotNull

@RunWith(AndroidJUnit4::class)
class OpenKattisServiceTests {

    @Test
    fun getOngoingContests_returnsContestsList() {
        // given
        val service = OpenKattisService()

        // when
        val result = service.ongoingContests

        // then
        assertNotNull(result)
    }

    @Test
    fun getContestStandings_returnsContestantsList() {
        // given
        val service = OpenKattisService()
        val contests = service.ongoingContests

        if (!contests!!.isEmpty()) {
            // when
            val result = service.getContestStandings(contests[0].contestId)

            // then
            assertNotNull(result)
        }
    }
}
