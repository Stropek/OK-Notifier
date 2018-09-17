package com.przemolab.oknotifier.modules

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.utils.DataHelper
import com.przemolab.oknotifier.utils.TestContentObserver

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import junit.framework.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class NotifierRepositoryTests {
    companion object {

        private val context = InstrumentationRegistry.getTargetContext()

        @JvmStatic
        @BeforeClass
        fun setUp() {
            DataHelper.deleteTablesDataOld(context)
        }

        @JvmStatic
        @AfterClass
        fun cleanUp() {
            DataHelper.deleteTablesDataOld(context)
        }
    }

    @Test
    fun getAll_returnsAllContest() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        for (i in 0..9) {
            DataHelper.insertContest(contentResolver, uri, "contest $i", "abc$i",
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i * 3, i * 2)
        }

        val notifierRepository = NotifierRepository(context)

        // when
        val result = notifierRepository.getAll(SortOrder.SubscribedFirst)

        // then
        assertEquals(10, result!!.size)
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
