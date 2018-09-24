package com.przemolab.oknotifier.modules

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.przemolab.oknotifier.data.AppDatabase

import com.przemolab.oknotifier.data.NotifierContract
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
    }

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun getAll_returnsAllContests() {
        // given
        db.contestDao().insertMany(DataHelper.createContestEntries(10))

        val notifierRepository = NotifierRepository(context)

        // when
        val result = notifierRepository.getAll()

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
