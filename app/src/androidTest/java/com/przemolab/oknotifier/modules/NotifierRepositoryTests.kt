package com.przemolab.oknotifier.modules

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.przemolab.oknotifier.data.AppDatabase

import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.utils.DataHelper

import org.junit.runner.RunWith

import junit.framework.Assert.assertEquals
import org.junit.*

@RunWith(AndroidJUnit4::class)
class NotifierRepositoryTests {

    private val context = InstrumentationRegistry.getTargetContext()
    private val db = AppDatabase.getInstance(context)!!

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(db)
    }

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun getAllContests_returnsAllContests() {
        // given
        db.contestDao().insertMany(DataHelper.createContestEntries(10))

        val notifierRepository = NotifierRepository(context)

        // when
        val result = notifierRepository.getAllContests()

        // then
        assertEquals(10, result!!.size)
    }

    @Test
    fun getSubscribed_returnsOnlySubscribedContests() {
        // given
        val contests = listOf(
                ContestEntry(contestId = "1", name = "subscribed", subscribed = true),
                ContestEntry(contestId = "2", name = "not subscribed", subscribed = false),
                ContestEntry(contestId = "3", name = "subscribed", subscribed = true),
                ContestEntry(contestId = "4", name = "not subscribed", subscribed = false)
        )
        db.contestDao().insertMany(contests)

        val notifierRepository = NotifierRepository(context)

        // when
        val result = notifierRepository.subscribed

        // then
        assertEquals(2, result!!.size)
    }

    @Test
    fun getAllContestants_contestIdPassed_returnsAllContestantsWithGivenContestId() {
        // given
        for (i in 1..10) {
            db.contestantDao().insert(DataHelper.createContestantEntry(i, if (i % 2 == 0) "abc" else "xyz"))
        }

        val notifierRepository = NotifierRepository(context)

        // when
        val result = notifierRepository.getAllContestants("abc")

        // then
        assertEquals(5, result!!.size)
        for (contestant in result) {
            assertEquals("abc", contestant.contestId)
        }
    }
}
