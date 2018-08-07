package com.przemolab.oknotifier.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContestDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contests.db";
    private static final int DATABASE_VERSION = 1;

    public ContestDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTESTS_TABLE = "CREATE TABLE " +
                ContestContract.ContestEntry.TABLE_NAME + "(" +
                ContestContract.ContestEntry._ID + " INTEGER PRIMARY KEY," +
                ContestContract.ContestEntry.COLUMN_CONTEST_ID + " VARCHAR(20) NOT NULL," +
                ContestContract.ContestEntry.COLUMN_CREATED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                ContestContract.ContestEntry.COLUMN_LAST_MODIFIED_DATE + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                ContestContract.ContestEntry.COLUMN_NAME + " VARCHAR(255) NOT NULL," +
                ContestContract.ContestEntry.COLUMN_START_DATE + " TEXT NOT NULL," +
                ContestContract.ContestEntry.COLUMN_END_DATE + " TEXT NOT NULL," +
                ContestContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS + " INT NOT NULL," +
                ContestContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS + " INT NOT NULL," +
                ContestContract.ContestEntry.COLUMN_IS_SUBSCRIBED + " BOOLEAN NOT NULL DEFAULT 0" +
                ");";

        db.execSQL(CREATE_CONTESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
