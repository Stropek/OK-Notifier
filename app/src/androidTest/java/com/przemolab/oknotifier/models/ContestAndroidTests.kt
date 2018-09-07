package com.przemolab.oknotifier.models

import android.content.ContentValues
import android.os.Parcel
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.utils.DataHelper
import com.przemolab.oknotifier.utils.DateUtils
import com.przemolab.oknotifier.utils.TestContentObserver

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import java.text.ParseException

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull

@RunWith(AndroidJUnit4::class)
class ContestAndroidTests {
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
    fun createFromParcel_returnsParceledContest() {
        // given
        val startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val endDate = DateUtils.getDate(2015, 6, 15, 16, 0, 0)
        val contest = Contest("abc", "name", startDate, endDate, 5, 10)

        // when
        val parcel = Parcel.obtain()
        contest.writeToParcel(parcel, contest.describeContents())
        parcel.setDataPosition(0)
        val fromParcel = Contest.CREATOR.createFromParcel(parcel)

        // then
        assertEquals(fromParcel.contestId, contest.contestId)
        assertEquals(fromParcel.name, contest.name)
        assertEquals(fromParcel.startDate, contest.startDate)
        assertEquals(fromParcel.endDate, contest.endDate)
        assertEquals(fromParcel.numberOfProblems, contest.numberOfProblems)
        assertEquals(fromParcel.numberOfContestants, contest.numberOfContestants)
        assertEquals(fromParcel.isSubscribed, contest.isSubscribed)
    }

    @Test
    fun newArray_returnsNewArrayOfGivenSize() {
        // when
        val contests = Contest.CREATOR.newArray(10)

        // then
        assertEquals(10, contests.size)
    }

    @Test
    @Throws(ParseException::class)
    fun getFromCursor_getsContestObjectFromCursor() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        val contentValues = ContentValues()
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NAME, "name")
        contentValues.put(NotifierContract.ContestEntry.COLUMN_CONTEST_ID, "abc")
        contentValues.put(NotifierContract.ContestEntry.COLUMN_START_DATE, "2010-10-16 16:00:00")
        contentValues.put(NotifierContract.ContestEntry.COLUMN_END_DATE, "2010-10-26 18:00:00")
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, 3)
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, 5)
        contentValues.put(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED, 1)
        contentResolver.insert(uri, contentValues)

        val contests = contentResolver.query(uri, null, null, null, null)

        assertNotNull(contests)
        contests!!.moveToFirst()

        // when
        val result = Contest.getFromCursor(contests)

        // then
        assertEquals(result.name, "name")
        assertEquals(result.contestId, "abc")
        assertEquals(result.startDateFormatted, "10/16/10 4:00PM")
        assertEquals(result.endDateFormatted, "10/26/10 6:00PM")
        assertEquals(result.numberOfContestants, 3)
        assertEquals(result.numberOfProblems, 5)
        assertEquals(result.isSubscribed, true)
    }

    @Test
    fun toContentValues_convertsObjectToContentValuesCollection() {
        // given
        val contest = createContest()

        // when
        val result = contest.toContentValues()

        // then
        assertEquals(7, result.size())
        assertEquals("name", result.get(NotifierContract.ContestEntry.COLUMN_NAME))
        assertEquals("abc", result.get(NotifierContract.ContestEntry.COLUMN_CONTEST_ID))
        assertEquals("2015-06-10 16:00:00", result.get(NotifierContract.ContestEntry.COLUMN_START_DATE))
        assertEquals("2015-06-15 17:00:00", result.get(NotifierContract.ContestEntry.COLUMN_END_DATE))
        assertEquals(5, result.get(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS))
        assertEquals(10, result.get(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS))
        assertEquals(false, result.get(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED))
    }

    private fun createContest(): Contest {
        val contestId = "abc"
        val name = "name"
        val startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0)
        val endDate = DateUtils.getDate(2015, 6, 15, 17, 0, 0)
        val numberOfContestants = 5
        val numberOfProblems = 10

        return Contest(contestId, name, startDate, endDate, numberOfContestants, numberOfProblems)
    }
}
