package com.przemolab.oknotifier.data

import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import com.przemolab.oknotifier.utils.DataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test

class ContestantDaoTests {

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
    fun getByContestId_contestantsInDatabase_returnsContestantsWithProvidedContestId() {
        // given
        //
        db.contestantDao().insert(DataHelper.createContestantEntry(1, contestId = "expected"))
        db.contestantDao().insert(DataHelper.createContestantEntry(2, contestId = "expected"))
        db.contestantDao().insert(DataHelper.createContestantEntry(3, contestId = "expected"))
        db.contestantDao().insert(DataHelper.createContestantEntry(4, contestId = "other"))
        db.contestantDao().insert(DataHelper.createContestantEntry(5, contestId = "other"))
        db.contestantDao().insert(DataHelper.createContestantEntry(6, contestId = "other"))

        // when
        val contestants = db.contestantDao().getByContestId("expected")

        // then
        assertNotNull(contestants)
        assertEquals(3, contestants.size)
    }

    @Test
    fun deleteAll_contestantsInDatabase_deletesAllContestants() {
        val existingContestants = DataHelper.createContestantEntries(10, "abc")
        db.contestantDao().insertMany(existingContestants)
        var contestants = db.contestantDao().getByContestId("abc")
        assertEquals(10, contestants.size)

        // when
        db.contestantDao().deleteAll()

        // then
        contestants = db.contestantDao().getByContestId("abc")
        assertNotNull(contestants)
        assertEquals(0, contestants.size)
    }

    @Test
    fun insert_contestantNotInDatabase_insertsContestant() {
        val newContestant = DataHelper.createContestantEntry(1, "abc")
        var contestants = db.contestantDao().getByContestId("abc")
        assertEquals(0, contestants.size)

        // when
        db.contestantDao().insert(newContestant)

        // then
        contestants = db.contestantDao().getByContestId("abc")
        assertNotNull(contestants)
        assertEquals(1, contestants.size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert_contestantInDatabase_shouldThrowSQLiteConstraintException() {
        val newContestant = DataHelper.createContestantEntry(1, "abc")
        db.contestantDao().insert(newContestant)
        val contestants = db.contestantDao().getByContestId("abc")
        assertEquals(1, contestants.size)

        // when
        db.contestantDao().insert(newContestant)
    }

    @Test
    fun insertMany_contestantsNotInDatabase_insertsContestants() {
        val newContestants = DataHelper.createContestantEntries(10, "abc")
        var contestants = db.contestantDao().getByContestId("abc")
        assertEquals(0, contestants.size)

        // when
        db.contestantDao().insertMany(newContestants)

        // then
        contestants = db.contestantDao().getByContestId("abc")
        assertNotNull(contestants)
        assertEquals(10, contestants.size)
    }

    @Test
    fun update_contestantInDatabase_updatesContestant() {
        val contestant = DataHelper.createContestantEntry(1, "abc")
        db.contestantDao().insert(contestant)
        val contestants = db.contestantDao().getByContestId("abc")
        assertEquals(1, contestants.size)

        // when
        contestant.name = "updated name"
        db.contestantDao().update(contestant)

        // then
        val updated = db.contestantDao().getByContestId("abc")
        assertNotNull(updated)
        assertEquals("updated name", updated[0].name)
    }
}