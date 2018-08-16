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

import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ContestantTests {

    private static final Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void createFromParcel_returnsParceledContestant() {
        // given
        Contestant contestant = new Contestant(1, "name", "abc", 1, 2, 3, 4 ,5);

        // when
        Parcel parcel = Parcel.obtain();
        contestant.writeToParcel(parcel, contestant.describeContents());
        parcel.setDataPosition(0);
        Contestant fromParcel = Contestant.CREATOR.createFromParcel(parcel);

        // then
        assertEquals(fromParcel.getId(), contestant.getId());
        assertEquals(fromParcel.getName(), contestant.getName());
        assertEquals(fromParcel.getContestId(), contestant.getContestId());
        assertEquals(fromParcel.getProblemsSolved(), contestant.getProblemsSolved());
        assertEquals(fromParcel.getProblemsSubmitted(), contestant.getProblemsSubmitted());
        assertEquals(fromParcel.getProblemsFailed(), contestant.getProblemsFailed());
        assertEquals(fromParcel.getProblemsNotTried(), contestant.getProblemsNotTried());
        assertEquals(fromParcel.getTime(), contestant.getTime());
    }

    @Test
    public void newArray_returnsNewArrayOfGivenSize() {
        // when
        Contestant[] contestants = Contestant.CREATOR.newArray(10);

        // then
        assertEquals(10, contestants.length);
    }

    @Test
    public void getFromCursor_getsContestObjectFromCursor() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, "name");
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, "abc");
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, 1);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, 2);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, 3);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, 4);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_TIME, 5);
        contentResolver.insert(uri, contentValues);

        Cursor contestants = contentResolver.query(uri, null, null, null, null);

        assertNotNull(contestants);
        contestants.moveToFirst();

        // when
        Contestant result = Contestant.getFromCursor(contestants);

        // then
        assertEquals(result.getName(), "name");
        assertEquals(result.getContestId(), "abc");
        assertEquals(result.getProblemsSolved(), 1);
        assertEquals(result.getProblemsSubmitted(), 2);
        assertEquals(result.getProblemsFailed(), 3);
        assertEquals(result.getProblemsNotTried(), 4);
        assertEquals(result.getTime(), 5);
    }
}