package com.przemolab.oknotifier.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.BuildConfig;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.przemolab.oknotifier.utils.DataHelper.deleteTablesData;
import static com.przemolab.oknotifier.utils.DataHelper.insertContest;
import static com.przemolab.oknotifier.utils.DataHelper.insertContestant;
import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class NotifierDataProviderTests {

    private static final Context context = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        deleteTablesData(context);
    }

    @AfterClass
    public static void cleanUp() {
        deleteTablesData(context);
    }

    @Test
    public void verifyProviderIsRegistered() throws PackageManager.NameNotFoundException {
        // given
        String packageName = context.getPackageName();
        String contestProviderClassName = NotifierDataProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, contestProviderClassName );

        // when
        ProviderInfo providerInfo = context.getPackageManager().getProviderInfo(componentName, 0);
        String actualAuthority = providerInfo.authority;

        // then
        assertEquals(packageName.replace("." + BuildConfig.FLAVOR, ""), actualAuthority);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insert_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        context.getContentResolver().insert(unknownUri, new ContentValues());
    }
    @Test
    public void insert_contest_shouldSucceed() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        // when
        Uri expectedUri = uri.buildUpon().appendPath("1").build();
        Uri actualUri = insertContest(contentResolver, uri, "test contest", "abc",
                "2018-07-30 18:00:00", "2018-08-15 18:00:00", 10, 20);

        // then
        assertEquals(expectedUri, actualUri);

        contentObserver.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(contentObserver);
    }
    @Test
    public void insert_contestant_shouldSucceed() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestantEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        // when
        Uri expectedUri = uri.buildUpon().appendPath("1").build();
        Uri actualUri = insertContestant(contentResolver, uri, "abc");

        // then
        assertEquals(expectedUri, actualUri);

        contentObserver.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(contentObserver);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void query_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        context.getContentResolver().query(unknownUri, null, null, null, null);
    }
    @Test
    public void query_contests_returnsAllContests() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            insertContest(contentResolver, uri, "contest " + i, "abc" + i,
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i*3, i*2);
        }

        // when
        Cursor contests = contentResolver.query(uri, null, null, null, null);

        // then
        assertNotNull(contests);
        assertEquals(10, contests.getCount());
    }
    @Test
    public void query_contestantsByContestId_returnsAllContestantsOfSpecifiedContest() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri insertUri = NotifierContract.ContestantEntry.CONTENT_URI;
        Uri queryUri = insertUri.buildUpon().appendPath("byContestId").appendPath("abc").build();

        setObservedUriOnContentResolver(contentResolver, insertUri, contentObserver);

        for (int i = 0; i < 10; i++) {
            String contestId = "abc";
            if (i % 2 == 0) {
                contestId = "xyz";
            }
            insertContestant(contentResolver, insertUri, contestId);
        }

        // when
        Cursor contestants = contentResolver.query(queryUri, null, null, null, null);

        // then
        assertNotNull(contestants);
        contestants.moveToFirst();
        assertEquals(5, contestants.getCount());
        assertEquals("abc", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void delete_withUnknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        context.getContentResolver().delete(unknownUri, null, null);
    }
    @Test
    public void delete_contestWithExistingId_shouldSucceed() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;
        Uri existingUri = uri.buildUpon().appendPath("byContestIds").build();

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            insertContest(contentResolver, uri, "contest " + i, "abc" + i,
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i*3, i*2);
        }

        // when
        int deleted = contentResolver.delete(existingUri, null, new String[] { "abc0", "abc1", "abc2", "abc3" });

        // then
        assertEquals(4, deleted);
        Cursor contests = contentResolver.query(uri, null, null, null, null);
        assertNotNull(contests);
        assertEquals(6, contests.getCount());
        contests.moveToFirst();
        assertEquals("abc4", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void update_withUnknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = NotifierContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        context.getContentResolver().update(unknownUri, null, null, null);
    }
    @Test
    public void update_contestWithExistingContestId_shouldOnlyUpdateNewValues() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        insertContest(contentResolver, uri, "test contest", "abc",
                "2018-07-30 18:00:00", "2018-08-15 18:00:00", 10, 20);

        ContentValues newValues = new ContentValues();
        newValues.put(NotifierContract.ContestEntry.COLUMN_NAME, "new name");

        // when
        int updated = contentResolver.update(uri, newValues,
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                new String[] {"abc"});

        // then
        assertEquals(1, updated);
        Cursor contests = contentResolver.query(uri, null, null, null, null);
        assertNotNull(contests);
        assertEquals(1, contests.getCount());
        contests.moveToFirst();
        assertEquals("new name", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME)));
        assertEquals("abc", contests.getString(contests.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)));
    }
    @Test
    public void update_contestantWithExistingContestantId_shouldOnlyUpdateNewValues() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestantEntry.CONTENT_URI;
        Uri queryUri = uri.buildUpon().appendPath("byContestId").appendPath("abc").build();

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        insertContestant(contentResolver, uri, "abc");
        insertContestant(contentResolver, uri, "abc");

        ContentValues newValues = new ContentValues();
        newValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, "mark twain");

        // when
        int updated = contentResolver.update(uri, newValues,
                NotifierContract.ContestantEntry._ID + "=?", new String[] {"1"});

        // then
        assertEquals(1, updated);
        Cursor contestants = contentResolver.query(queryUri, null, null, null, null);
        assertNotNull(contestants);
        assertEquals(2, contestants.getCount());
        contestants.moveToFirst();
        assertEquals("mark twain", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME)));
        assertEquals("abc", contestants.getString(contestants.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)));
    }
}
