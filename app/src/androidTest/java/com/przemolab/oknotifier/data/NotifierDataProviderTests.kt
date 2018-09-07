package com.przemolab.oknotifier.data

import android.content.ComponentName
import android.content.ContentValues
import android.content.pm.PackageManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.BuildConfig
import com.przemolab.oknotifier.utils.DataHelper
import com.przemolab.oknotifier.utils.TestContentObserver

import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull

@RunWith(AndroidJUnit4::class)
class NotifierDataProviderTests {
    companion object {

        private val context = InstrumentationRegistry.getTargetContext()

        @AfterClass
        @JvmStatic
        fun cleanUp() {
            DataHelper.deleteTablesData(context)
        }
    }

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(context)
    }

    @Test
    @Throws(PackageManager.NameNotFoundException::class)
    fun verifyProviderIsRegistered() {
        // given
        val packageName = context.packageName
        val contestProviderClassName = NotifierDataProvider::class.java.name
        val componentName = ComponentName(packageName, contestProviderClassName)

        // when
        val providerInfo = context.packageManager.getProviderInfo(componentName, 0)
        val actualAuthority = providerInfo.authority

        // then
        assertEquals(packageName.replace("." + BuildConfig.FLAVOR, ""), actualAuthority)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun insert_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        val unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build()
        DataHelper.setObservedUriOnContentResolver(context.contentResolver, unknownUri, TestContentObserver.testContentObserver)

        // when
        context.contentResolver.insert(unknownUri, ContentValues())
    }

    @Test
    fun insert_contest_shouldSucceed() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        // when
        val expectedUri = uri.buildUpon().appendPath("1").build()
        val actualUri = DataHelper.insertContest(contentResolver, uri, "test contest", "abc",
                "2018-07-30 18:00:00", "2018-08-15 18:00:00", 10, 20)

        // then
        assertEquals(expectedUri, actualUri)

        contentObserver.waitForNotificationOrFail()
        contentResolver.unregisterContentObserver(contentObserver)
    }

    @Test
    fun insert_contestant_shouldSucceed() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestantEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        // when
        val expectedUri = uri.buildUpon().appendPath("1").build()
        val actualUri = DataHelper.insertContestant(contentResolver, uri, "abc")

        // then
        assertEquals(expectedUri, actualUri)

        contentObserver.waitForNotificationOrFail()
        contentResolver.unregisterContentObserver(contentObserver)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun query_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        val unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build()
        DataHelper.setObservedUriOnContentResolver(context.contentResolver, unknownUri, TestContentObserver.testContentObserver)

        // when
        context.contentResolver.query(unknownUri, null, null, null, null)
    }

    @Test
    fun query_contests_returnsAllContests() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        for (i in 0..9) {
            DataHelper.insertContest(contentResolver, uri, "contest $i", "abc$i",
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i * 3, i * 2)
        }

        // when
        val contests = contentResolver.query(uri, null, null, null, null)

        // then
        assertNotNull(contests)
        assertEquals(10, contests!!.count)
    }

    @Test
    fun query_contestantsByContestId_returnsAllContestantsOfSpecifiedContest() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val insertUri = NotifierContract.ContestantEntry.CONTENT_URI
        val queryUri = insertUri.buildUpon().appendPath("byContestId").appendPath("abc").build()

        DataHelper.setObservedUriOnContentResolver(contentResolver, insertUri, contentObserver)

        for (i in 0..9) {
            var contestId = "abc"
            if (i % 2 == 0) {
                contestId = "xyz"
            }
            DataHelper.insertContestant(contentResolver, insertUri, contestId)
        }

        // when
        val contestants = contentResolver.query(queryUri, null, null, null, null)

        // then
        assertNotNull(contestants)
        contestants!!.moveToFirst()
        assertEquals(5, contestants.count)
        assertEquals("abc", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID)))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun delete_withUnknownUri_shouldThrowUnsupportedOperationException() {
        // given
        val unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build()
        DataHelper.setObservedUriOnContentResolver(context.contentResolver, unknownUri, TestContentObserver.testContentObserver)

        // when
        context.contentResolver.delete(unknownUri, null, null)
    }

    @Test
    fun delete_contestWithExistingId_shouldSucceed() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI
        val existingUri = uri.buildUpon().appendPath("byContestIds").build()

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        for (i in 0..9) {
            DataHelper.insertContest(contentResolver, uri, "contest $i", "abc$i",
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i * 3, i * 2)
        }

        // when
        val deleted = contentResolver.delete(existingUri, null, arrayOf("abc0", "abc1", "abc2", "abc3"))

        // then
        assertEquals(4, deleted)
        val contests = contentResolver.query(uri, null, null, null, null)
        assertNotNull(contests)
        assertEquals(6, contests!!.count)
        contests.moveToFirst()
        assertEquals("abc4", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun update_withUnknownUri_shouldThrowUnsupportedOperationException() {
        // given
        val unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build()
        DataHelper.setObservedUriOnContentResolver(context.contentResolver, unknownUri, TestContentObserver.testContentObserver)

        // when
        context.contentResolver.update(unknownUri, null, null, null)
    }

    @Test
    fun update_contestWithExistingContestId_shouldOnlyUpdateNewValues() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestEntry.CONTENT_URI

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        DataHelper.insertContest(contentResolver, uri, "test contest", "abc",
                "2018-07-30 18:00:00", "2018-08-15 18:00:00", 10, 20)

        val newValues = ContentValues()
        newValues.put(NotifierContract.ContestEntry.COLUMN_NAME, "new name")

        // when
        val updated = contentResolver.update(uri, newValues,
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                arrayOf("abc"))

        // then
        assertEquals(1, updated)
        val contests = contentResolver.query(uri, null, null, null, null)
        assertNotNull(contests)
        assertEquals(1, contests!!.count)
        contests.moveToFirst()
        assertEquals("new name", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME)))
        assertEquals("abc", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)))
    }

    @Test
    fun update_contestantWithExistingContestantId_shouldOnlyUpdateNewValues() {
        // given
        val contentResolver = context.contentResolver
        val contentObserver = TestContentObserver.testContentObserver
        val uri = NotifierContract.ContestantEntry.CONTENT_URI
        val queryUri = uri.buildUpon().appendPath("byContestId").appendPath("abc").build()

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver)

        DataHelper.insertContestant(contentResolver, uri, "abc")
        DataHelper.insertContestant(contentResolver, uri, "abc")

        val newValues = ContentValues()
        newValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, "mark twain")

        // when
        val updated = contentResolver.update(uri, newValues,
                NotifierContract.ContestantEntry._ID + "=?", arrayOf("1"))

        // then
        assertEquals(1, updated)
        val contestants = contentResolver.query(queryUri, null, null, null, null)
        assertNotNull(contestants)
        assertEquals(2, contestants!!.count)
        contestants.moveToFirst()
        assertEquals("mark twain", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME)))
        assertEquals("abc", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)))
    }
}
