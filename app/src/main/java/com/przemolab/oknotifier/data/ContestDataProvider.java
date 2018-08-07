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

import timber.log.Timber;

public class ContestDataProvider extends ContentProvider {

    private Context _context;

    private static ContestDbHelper _db;
    private static UriMatcher _uriMatcher = buildUriMatcher();

    private static final int CONTEST_ENTRIES = 100;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ContestContract.AUTHORITY, ContestContract.PATH_CONTESTS, CONTEST_ENTRIES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        _context = getContext();
        _db = new ContestDbHelper(_context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = _db.getReadableDatabase();
        Cursor cursor;

        switch (_uriMatcher.match(uri)) {
            case CONTEST_ENTRIES:
                cursor = db.query(ContestContract.ContestEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
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
        SQLiteDatabase db = _db.getWritableDatabase();

        switch (_uriMatcher.match(uri)) {
            case CONTEST_ENTRIES:
                long id = db.insert(ContestContract.ContestEntry.TABLE_NAME, null, values);
                if (id > -1) {
                    returnUri = uri.buildUpon().appendPath(String.format("%s", id)).build();
                } else {
                    throw new SQLException("Failed to insert a new contest into: " + uri);
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
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
