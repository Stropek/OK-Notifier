package com.przemolab.oknotifier.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.data.NotifierDbHelper;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class DataHelper {

    public static Contest createContest(int id) {
        return createContest(id, false);
    }

    public static Contest createContest(int id, boolean subscribed) {
        String idString = String.format("id %s", id);
        String name = String.format("id %s", id);
        Date startDate = DateUtils.getDate(2000 + id, id, id, id, id, 0);
        Date endDate = DateUtils.getDate(2001 + id, id + 1, id + 1, id + 1, id + 1, 0);

        Contest contest = new Contest(idString, name, startDate, endDate, id, id);
        contest.setSubscribed(subscribed);

        return contest;
    }

    public static List<Contest> createContests(int count) {
        List<Contest> contests = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            contests.add(createContest(i));
        }

        return contests;
    }

    public static Contestant createContestant(int id, String contestId) {
        String name = String.format("name %s", id);

        return new Contestant(id, name, contestId, 1, 2, 3, 4, 5);
    }

    public static List<Contestant> createContestants(int count, String contestId) {
        List<Contestant> contestants = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            contestants.add(createContestant(i, contestId));
        }

        return contestants;
    }

    public static Uri insertContest(ContentResolver contentResolver, Uri uri, Contest contest) {
        return insertContest(contentResolver, uri,
                contest.getName(),
                contest.getContestId(),
                DateUtils.formatDate(contest.getStartDate(), DateUtils.SQLiteDateTimeFormat),
                DateUtils.formatDate(contest.getEndDate(), DateUtils.SQLiteDateTimeFormat),
                contest.getNumberOfContestants(),
                contest.getNumberOfProblems(),
                contest.isSubscribed());
    }

    public static Uri insertContest(ContentResolver contentResolver, Uri uri, String name, String id,
                                    String startDate, String endDate, int numOfContestants, int numOfProblems) {
        return insertContest(contentResolver, uri, name, id, startDate, endDate, numOfContestants, numOfProblems, false);
    }

    public static Uri insertContest(ContentResolver contentResolver, Uri uri, String name, String id,
                              String startDate, String endDate, int numOfContestants, int numOfProblems,
                                    boolean isSubscribed) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(NotifierContract.ContestEntry.COLUMN_NAME, name);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_CONTEST_ID, id);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_START_DATE, startDate);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_END_DATE, endDate);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, numOfContestants);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, numOfProblems);
        contentValues.put(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED, isSubscribed);

        return contentResolver.insert(uri, contentValues);
    }

    public static Uri insertContestant(ContentResolver contentResolver, Uri uri, String contestId) {
        return insertContestant(contentResolver, uri, contestId, "john doe");
    }

    public static Uri insertContestant(ContentResolver contentResolver, Uri uri, String contestId, String name) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NotifierContract.ContestantEntry.COLUMN_NAME, name);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, contestId);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, 1);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, 2);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, 3);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, 4);
        contentValues.put(NotifierContract.ContestantEntry.COLUMN_TIME, 5);

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

    public static void deleteTablesData(Context context) {
        NotifierDbHelper dbHelper = new NotifierDbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // cleanup contests before tests start
        database.delete(NotifierContract.ContestEntry.TABLE_NAME, null, null);
        database.delete(NotifierContract.ContestantEntry.TABLE_NAME, null, null);
    }
}
