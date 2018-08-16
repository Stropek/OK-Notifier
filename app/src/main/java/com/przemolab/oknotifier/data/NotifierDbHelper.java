package com.przemolab.oknotifier.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotifierDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contests.db";
    private static final int DATABASE_VERSION = 1;

    public NotifierDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTESTS_TABLE = "CREATE TABLE " +
                NotifierContract.ContestEntry.TABLE_NAME + "(" +
                NotifierContract.ContestEntry._ID + " INTEGER PRIMARY KEY," +
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + " VARCHAR(20) UNIQUE NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_CREATED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                NotifierContract.ContestEntry.COLUMN_LAST_MODIFIED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                NotifierContract.ContestEntry.COLUMN_NAME + " VARCHAR(255) NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_START_DATE + " TEXT NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_END_DATE + " TEXT NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS + " INT NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS + " INT NOT NULL," +
                NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED + " BOOLEAN NOT NULL DEFAULT 0" +
                ");";

        final String CREATE_CONTESTANTS_TABLE = "CREATE TABLE " +
                NotifierContract.ContestantEntry.TABLE_NAME + "(" +
                NotifierContract.ContestantEntry._ID + " INTEGER PRIMARY KEY," +
                NotifierContract.ContestantEntry.COLUMN_NAME + " VARCHAR(255) NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_CONTEST_ID+ " VARCHAR(20) NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_CREATED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                NotifierContract.ContestantEntry.COLUMN_LAST_MODIFIED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED + " INT NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED + " INT NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED + " INT NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED + " INT NOT NULL," +
                NotifierContract.ContestantEntry.COLUMN_TIME + " INT NOT NULL," +
                    "FOREIGN KEY (" + NotifierContract.ContestantEntry.COLUMN_CONTEST_ID + ") " +
                    "REFERENCES " + NotifierContract.ContestEntry.TABLE_NAME + "(" + NotifierContract.ContestEntry.COLUMN_CONTEST_ID + ")" +
                ");";

        db.execSQL(CREATE_CONTESTS_TABLE);
        db.execSQL(CREATE_CONTESTANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
