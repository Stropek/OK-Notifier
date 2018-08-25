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
        Uri insertUri = NotifierContract.ContestantEntry.CONTENT_URI;
        Uri queryUri =  insertUri.buildUpon().appendPath("byContestId").appendPath("abc").build();

        setObservedUriOnContentResolver(contentResolver, insertUri, contentObserver);

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, "name");
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, "abc");
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, 1);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, 2);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, 3);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, 4);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_TIME, 5);
        contentResolver.insert(insertUri, contentValues);

        Cursor contestants = contentResolver.query(queryUri, null, null, null, null);

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

    @Test
    public void toContentValues_convertsObjectToContentValuesCollection() {
        // given
        Contestant contestant = createContestant();

        // when
        ContentValues result = contestant.toContentValues();

        // then
        assertEquals(7, result.size());
        assertEquals("name", result.get(NotifierContract.ContestantEntry.COLUMN_NAME));
        assertEquals("abc", result.get(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID));
        assertEquals(1, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED));
        assertEquals(2, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED));
        assertEquals(3, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED));
        assertEquals(4, result.get(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED));
        assertEquals(5, result.get(NotifierContract.ContestantEntry.COLUMN_TIME));
    }

    private Contestant createContestant() {
        String name = "name";
        String contestId = "abc";
        int approved = 1;
        int submitted = 2;
        int rejected = 3;
        int notTried = 4;
        int time = 5;

        return new Contestant(0, name, contestId, approved, submitted, rejected, notTried, time);
    }
}