package com.przemolab.oknotifier.modules

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.przemolab.oknotifier.data.AppDatabase

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.utils.DataHelper
import com.przemolab.oknotifier.utils.TestContentObserver

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
        // TODO: remove when Contestant is also changed to ContestantEntry
        DataHelper.deleteTablesDataOld(context)
    }

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
        // TODO: remove when Contestant is also changed to ContestantEntry
        DataHelper.deleteTablesDataOld(context)
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
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestantEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        for (i in 0..9) {
            if (i % 2 == 0) {
                DataHelper.insertContestant(contentResolver, uri, "abc")
            } else {
                DataHelper.insertContestant(contentResolver, uri, "zxy")
            }
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
