package com.przemolab.oknotifier.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.data.ContestContract;
import com.przemolab.oknotifier.utils.DateUtils;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.przemolab.oknotifier.utils.DataHelper.deleteTablesData;
import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ContestTests {

    private static final Context context = InstrumentationRegistry.getTargetContext();

    @BeforeClass
    public static void setUp() {
        deleteTablesData(context);
    }

    @AfterClass
    public static void cleanUp() {
        deleteTablesData(context);
    }

    @Test
    public void createFromParcel_returnsParceledContest() {
        // given
        Date startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0);
        Date endDate = DateUtils.getDate(2015, 6, 15, 16, 0, 0);
        Contest contest = new Contest("abc", "name", startDate, endDate, 5, 10);

        // when
        Parcel parcel = Parcel.obtain();
        contest.writeToParcel(parcel, contest.describeContents());
        parcel.setDataPosition(0);
        Contest fromParcel = Contest.CREATOR.createFromParcel(parcel);

        // then
        assertEquals(fromParcel.getId(), contest.getId());
        assertEquals(fromParcel.getName(), contest.getName());
    }

    @Test
    public void newArray_returnsNewArrayOfGivenSize() {
        // when
        Contest[] contests = Contest.CREATOR.newArray(10);

        // then
        assertEquals(10, contests.length);
    }

    @Test
    public void getFromCursor_getsContestObjectFromCursor() throws ParseException {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContestContract.ContestEntry.COLUMN_NAME, "name");
        contentValues.put(ContestContract.ContestEntry.COLUMN_CONTEST_ID, "abc");
        contentValues.put(ContestContract.ContestEntry.COLUMN_START_DATE, "2010-10-16 16:00:00");
        contentValues.put(ContestContract.ContestEntry.COLUMN_END_DATE, "2010-10-26 18:00:00");
        contentValues.put(ContestContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, 3);
        contentValues.put(ContestContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, 5);
        contentValues.put(ContestContract.ContestEntry.COLUMN_IS_SUBSCRIBED, 1);
        contentResolver.insert(uri, contentValues);

        Cursor contests = contentResolver.query(uri, null, null, null, null);

        assertNotNull(contests);
        contests.moveToFirst();

        // when
        Contest result = Contest.getFromCursor(contests);

        // then
        assertEquals(result.getName(), "name");
        assertEquals(result.getId(), "abc");
        assertEquals(result.getStartDateFormatted(), "10/16/10 4:00PM");
        assertEquals(result.getEndDateFormatted(), "10/26/10 6:00PM");
        assertEquals(result.getNumberOfContestants(), 3);
        assertEquals(result.getNumberOfProblems(), 5);
        assertEquals(result.isSubscribed(), true);
    }
}
