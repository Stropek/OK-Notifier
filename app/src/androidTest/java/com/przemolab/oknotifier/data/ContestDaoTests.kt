package com.przemolab.oknotifier.data

import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.przemolab.oknotifier.utils.DataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContestDaoTests {
    private val context = InstrumentationRegistry.getTargetContext()
    private val db = AppDatabase.getInstance(context)!!

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
    }

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun getAll_noContestsInDatabase_returnsEmptyList() {
        // when
        val contests = db.contestDao().getAll()

        // then
        assertNotNull(contests)
        assertEquals(0, contests.size)
    }

    @Test
    fun getAll_contestsInDatabase_returnsContestsList() {
        // given
        val existingContests = DataHelper.createContestEntries(10, subscribed = false)
        db.contestDao().insertMany(existingContests)

        // when
        val contests = db.contestDao().getAll()

        // then
        assertNotNull(contests)
        assertEquals(10, contests.size)
    }

    @Test
    fun getSubscribed_noSubscribedContestsInDatabase_returnsEmptyList() {
        // given
        val existingContests = DataHelper.createContestEntries(10, subscribed = false)
        db.contestDao().insertMany(existingContests)

        // when
        val contests = db.contestDao().getSubscribed()

        // then
        assertNotNull(contests)
        assertEquals(0, contests.size)
    }

    @Test
    fun getSubscribed_subscribedContestsInDatabase_returnsContestsList() {
        // given
        val existingContests = DataHelper.createContestEntries(10, subscribed = true)
        db.contestDao().insertMany(existingContests)

        // when
        val contests = db.contestDao().getSubscribed()

        // then
        assertNotNull(contests)
        assertEquals(10, contests.size)
    }

    @Test
    fun deleteAll_contestsInDatabase_deletesAllContests() {
        val existingContests = DataHelper.createContestEntries(10)
        db.contestDao().insertMany(existingContests)
        var contests = db.contestDao().getAll()
        assertEquals(10, contests.size)

        // when
        db.contestDao().deleteAll()

        // then
        contests = db.contestDao().getAll()
        assertNotNull(contests)
        assertEquals(0, contests.size)
    }

    @Test
    fun deleteMany_someContestsInDatabase_deletesContests() {
        val existingContests = DataHelper.createContestEntries(10)
        val inDatabase = existingContests.subList(0, 5)
        db.contestDao().insertMany(existingContests)
        var contests = db.contestDao().getAll()
        assertEquals(10, contests.size)

        // when
        db.contestDao().deleteMany(inDatabase)

        // then
        contests = db.contestDao().getAll()
        assertNotNull(contests)
        assertEquals(5, contests.size)
    }

    @Test
    fun insert_contestNotInDatabase_insertsContest() {
        val newContest = DataHelper.createContestEntry(1)
        var contests = db.contestDao().getAll()
        assertEquals(0, contests.size)

        // when
        db.contestDao().insert(newContest)

        // then
        contests = db.contestDao().getAll()
        assertNotNull(contests)
        assertEquals(1, contests.size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert_contestInDatabase_shouldThrowSQLiteConstraintException() {
        val newContest = DataHelper.createContestEntry(1)
        db.contestDao().insert(newContest)
        val contests = db.contestDao().getAll()
        assertEquals(1, contests.size)

        // when
        db.contestDao().insert(newContest)
    }

    @Test
    fun insertMany_contestsNotInDatabase_insertsContests() {
        val newContests = DataHelper.createContestEntries(10)
        var contests = db.contestDao().getAll()
        assertEquals(0, contests.size)

        // when
        db.contestDao().insertMany(newContests)

        // then
        contests = db.contestDao().getAll()
        assertNotNull(contests)
        assertEquals(10, contests.size)
    }

    @Test
    fun update_contestInDatabase_updatesContest() {
        val contest = DataHelper.createContestEntry(1)
        db.contestDao().insert(contest)
        val contests = db.contestDao().getAll()
        assertEquals(1, contests.size)

        // when
        contest.name = "updated name"
        db.contestDao().update(contest)

        // then
        val updated = db.contestDao().getAll()
        assertNotNull(updated)
        assertEquals("updated name", updated[0].name)
    }

    @Test
    fun updateNumberOfContestants_contestInDatabase_updatesNumberOfContestantsOnGivenContest() {
        val contest = DataHelper.createContestEntry(1, contestId = "abc")
        db.contestDao().insert(contest)
        val contests = db.contestDao().getAll()
        assertEquals(1, contests.size)
        assertEquals(0, contests[0].numberOfContestants)

        // when
        db.contestDao().updateNumberOfContestants("abc", 10)

        // then
        val updated = db.contestDao().getAll()
        assertNotNull(updated)
        assertEquals(10, updated[0].numberOfContestants)
    }
}