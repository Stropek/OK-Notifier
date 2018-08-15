package com.przemolab.oknotifier.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NotifierDataProvider extends ContentProvider {

    private Context _context;

    private static NotifierDbHelper db;
    private static UriMatcher uriMatcher = buildUriMatcher();

    private static final int CONTEST_ENTRIES = 100;
    private static final int CONTEST_ENTRIES_BY_IDS = 101;

    private static final int CONTESTANT_ENTRIES = 200;
    private static final int CONTESTANT_ENTRIES_BY_CONTEST_ID = 201;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTS, CONTEST_ENTRIES);
        uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTS + "/byContestIds", CONTEST_ENTRIES_BY_IDS);

        uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTANTS, CONTESTANT_ENTRIES);
        uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTANTS + "/byContestId/*", CONTESTANT_ENTRIES_BY_CONTEST_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        _context = getContext();
        db = new NotifierDbHelper(_context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = NotifierDataProvider.db.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CONTEST_ENTRIES:
                cursor = db.query(NotifierContract.ContestEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CONTESTANT_ENTRIES_BY_CONTEST_ID:
                String contestId = uri.getLastPathSegment();
                cursor = db.query(NotifierContract.ContestantEntry.TABLE_NAME,
                        projection,
                        NotifierContract.ContestantEntry.COLUMN_CONTEST_ID +"=?",
                        new String[] { contestId },
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown operation URI: " + uri);
        }

        if (cursor != null) {
            if (_context != null) {
                cursor.setNotificationUri(_context.getContentResolver(), uri);
            }
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        SQLiteDatabase db = NotifierDataProvider.db.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CONTEST_ENTRIES:
                long contestId = db.insert(NotifierContract.ContestEntry.TABLE_NAME, null, values);
                if (contestId > -1) {
                    returnUri = uri.buildUpon().appendPath(String.format("%s", contestId)).build();
                } else {
                    throw new SQLException("Failed to insert a new contest into: " + uri);
                }
                break;
            case CONTESTANT_ENTRIES:
                long contestantId = db.insert(NotifierContract.ContestantEntry.TABLE_NAME, null, values);
                if (contestantId > -1) {
                    returnUri = uri.buildUpon().appendPath(String.format("%s", contestantId)).build();
                } else {
                    throw new SQLException("Failed to insert a new contestant into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown operation URI: " + uri);
        }

        _context.getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted = 0;
        SQLiteDatabase db = NotifierDataProvider.db.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CONTEST_ENTRIES_BY_IDS:
                StringBuilder inCondition = new StringBuilder();

                if (selectionArgs != null && selectionArgs.length > 0) {
                    for (String arg : selectionArgs) {
                        inCondition.append("'").append(arg).append("',");
                    }
                    inCondition.deleteCharAt(inCondition.lastIndexOf(","));

                    deleted = db.delete(NotifierContract.ContestEntry.TABLE_NAME,
                            NotifierContract.ContestEntry.COLUMN_CONTEST_ID + " IN (" + inCondition.toString() + ")",
                            null);
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown operation URI: " + uri);
        }

        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updated;
        SQLiteDatabase db = NotifierDataProvider.db.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CONTEST_ENTRIES:
                updated = db.update(NotifierContract.ContestEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown operation URI: " + uri);
        }

        return updated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
