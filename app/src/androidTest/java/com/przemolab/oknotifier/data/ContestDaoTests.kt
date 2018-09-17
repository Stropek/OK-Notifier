package com.przemolab.oknotifier.data

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.przemolab.oknotifier.utils.DataHelper
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContestDaoTests {
    companion object {

        private val context = InstrumentationRegistry.getTargetContext()
        private val db = AppDatabase.getInstance(context)!!

        @AfterClass
        @JvmStatic
        fun cleanUp() {
            DataHelper.deleteTablesData(db)
        }
    }

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun getAll_noContests_returnsEmptyList() {
        // when
        val contests = db.contestDao().getAll()

        // then
        assertNotNull(contests)
        assertEquals(0, contests.count())
    }

    @Test
    fun getAll_contestsInDatabase_returnsContestsList() {
        // given
        val existingContests = DataHelper.createContestEntries(10)
        db.contestDao().insertAll(existingContests)

        // when
        val contests = db.contestDao().getAll()

        // then
        assertNotNull(contests)
        assertEquals(10, contests.count())
    }
}