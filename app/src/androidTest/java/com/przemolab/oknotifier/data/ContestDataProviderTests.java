package com.przemolab.oknotifier.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ContestDataProviderTests {

    private final Context _context = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        ContestDbHelper dbHelper = new ContestDbHelper(_context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // cleanup contests before tests start
        database.delete(ContestContract.ContestEntry.TABLE_NAME, null, null);
    }

    @Test
    public void verifyProviderIsRegistered() throws PackageManager.NameNotFoundException {
        // given
        String packageName = _context.getPackageName();
        String contestProviderClassName = ContestDataProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, contestProviderClassName );

        // when
        ProviderInfo providerInfo = _context.getPackageManager().getProviderInfo(componentName, 0);
        String actualAuthority = providerInfo.authority;

        // then
        assertEquals(packageName, actualAuthority);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insert_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = ContestContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(_context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        _context.getContentResolver().insert(unknownUri, new ContentValues());
    }
    @Test
    public void insert_withValidParameters_shouldSucceed() {
        // given
        ContentResolver contentResolver = _context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

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

    @Test(expected = UnsupportedOperationException.class)
    public void query_unknownUri_shouldThrowUnsupportedOperationException() {
        // given
        Uri unknownUri = ContestContract.BASE_CONTENT_URI.buildUpon().appendPath("unknown").build();
        setObservedUriOnContentResolver(_context.getContentResolver(), unknownUri, TestContentObserver.getTestContentObserver());

        // when
        _context.getContentResolver().query(unknownUri, null, null, null, null);
    }
    @Test
    public void query_withContestsContentUri_returnsAllContests() {
        // given
        ContentResolver contentResolver = _context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            insertContest(contentResolver, uri, "contest " + i, "abc" + i,
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i*3, i*2);
        }

        // when
        Cursor contests = contentResolver.query(uri, null, null, null, null);

        // then
        assertNotNull(contests);
        assertEquals(contests.getCount(), 10);
    }

    private Uri insertContest(ContentResolver contentResolver, Uri uri, String name, String id,
                              String startDate, String endDate, int numOfContestants, int numOfProblems) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContestContract.ContestEntry.COLUMN_NAME, name);
        contentValues.put(ContestContract.ContestEntry.COLUMN_CONTEST_ID, id);
        contentValues.put(ContestContract.ContestEntry.COLUMN_START_DATE, startDate);
        contentValues.put(ContestContract.ContestEntry.COLUMN_END_DATE, endDate);
        contentValues.put(ContestContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, numOfContestants);
        contentValues.put(ContestContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, numOfProblems);

        return contentResolver.insert(uri, contentValues);
    }

    private void setObservedUriOnContentResolver(ContentResolver contentResolver, Uri uri, ContentObserver contentObserver) {
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                uri,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                contentObserver);
    }
}
