package com.przemolab.oknotifier.models

import android.content.ContentValues
import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.utils.DataHelper
import com.przemolab.oknotifier.utils.TestContentObserver

import org.junit.Test
import org.junit.runner.RunWith

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.AfterClass
import org.junit.BeforeClass

@RunWith(AndroidJUnit4::class)
class ContestantAndroidTests {
    companion object {
        private val context = InstrumentationRegistry.getTargetContext()

        @JvmStatic
        @BeforeClass
        fun setUp() {
            DataHelper.deleteTablesData(context)
        }

        @JvmStatic
        @AfterClass
        fun cleanUp() {
            DataHelper.deleteTablesData(context)
        }
    }

    @Test
    fun createFromParcel_returnsParceledContestant() {
        // given
        val contestant = Contestant("name", "abc", 1, 2, 3, 4, 5)

        // when
        val parcel = Parcel.obtain()
        contestant.writeToParcel(parcel, contestant.describeContents())
        parcel.setDataPosition(0)
        val fromParcel = Contestant.CREATOR.createFromParcel(parcel)

        // then
        assertEquals(fromParcel.name, contestant.name)
        assertEquals(fromParcel.contestId, contestant.contestId)
        assertEquals(fromParcel.problemsSolved, contestant.problemsSolved)
        assertEquals(fromParcel.problemsSubmitted, contestant.problemsSubmitted)
        assertEquals(fromParcel.problemsFailed, contestant.problemsFailed)
        assertEquals(fromParcel.problemsNotTried, contestant.problemsNotTried)
        assertEquals(fromParcel.time, contestant.time)
    }

    @Test
    fun newArray_returnsNewArrayOfGivenSize() {
        // when
        val contestants = Contestant.CREATOR.newArray(10)

        // then
        assertEquals(10, contestants.size)
    }

    @Test
    fun getFromCursor_getsContestObjectFromCursor() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val insertUri = NotifierContract.ContestantEntry.CONTENT_URI
        val queryUri = insertUri.buildUpon().appendPath("byContestId").appendPath("abc").build()

        DataHelper.setObservedUriOnContentResolver(contentResolver, insertUri, contentObserver)

        val contentValues = ContentValues()
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, "name")
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, "abc")
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, 1)
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, 2)
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, 3)
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, 4)
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_TIME, 5)
        contentResolver.insert(insertUri, contentValues)

        val contestants = contentResolver.query(queryUri, null, null, null, null)

        assertNotNull(contestants)
        contestants!!.moveToFirst()

        // when
        val result = Contestant.getFromCursor(contestants)

        // then
        assertEquals(result.name, "name")
        assertEquals(result.contestId, "abc")
        assertEquals(result.problemsSolved, 1)
        assertEquals(result.problemsSubmitted, 2)
        assertEquals(result.problemsFailed, 3)
        assertEquals(result.problemsNotTried, 4)
        assertEquals(result.time, 5)
    }

    @Test
    fun toContentValues_convertsObjectToContentValuesCollection() {
        // given
        val contestant = createContestant()

        // when
        val result = contestant.toContentValues()

        // then
        assertEquals(7, result.size())
        assertEquals("name", result.get(NotifierContract.ContestantEntry.COLUMN_NAME))
        assertEquals("abc", result.get(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID))
        assertEquals(1, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED))
        assertEquals(2, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED))
        assertEquals(3, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED))
        assertEquals(4, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED))
        assertEquals(5, result.get(NotifierContract.ContestantEntry.COLUMN_TIME))
    }

    private fun createContestant(): Contestant {
        val name = "name"
        val contestId = "abc"
        val approved = 1
        val submitted = 2
        val rejected = 3
        val notTried = 4
        val time = 5

        return Contestant(name, contestId, approved, submitted, rejected, notTried, time)
    }
}