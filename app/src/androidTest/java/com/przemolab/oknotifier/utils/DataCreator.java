package com.przemolab.oknotifier.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.net.Uri;

import com.przemolab.oknotifier.data.ContestContract;

public abstract class DataCreator {

    public static Uri insertContest(ContentResolver contentResolver, Uri uri, String name, String id,
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

    public static void setObservedUriOnContentResolver(ContentResolver contentResolver, Uri uri, ContentObserver contentObserver) {
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                uri,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                contentObserver);
    }
}
