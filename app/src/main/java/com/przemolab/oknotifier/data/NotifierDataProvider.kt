package com.przemolab.oknotifier.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri

class NotifierDataProvider : ContentProvider() {

    private var _context: Context? = null

    override fun onCreate(): Boolean {
        _context = context
        db = NotifierDbHelper(_context!!)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = NotifierDataProvider.db!!.readableDatabase
        val cursor: Cursor?

        when (uriMatcher.match(uri)) {
            CONTEST_ENTRIES -> cursor = db.query(NotifierContract.ContestEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs, null, null,
                    sortOrder)
            CONTESTANT_ENTRIES_BY_CONTEST_ID -> {
                val contestId = uri.lastPathSegment
                cursor = db.query(NotifierContract.ContestantEntry.TABLE_NAME,
                        projection,
                        NotifierContract.ContestantEntry.COLUMN_CONTEST_ID + "=?",
                        arrayOf(contestId), null, null,
                        sortOrder)
            }
            else -> throw UnsupportedOperationException("Unknown operation URI: $uri")
        }

        if (cursor != null) {
            if (_context != null) {
                cursor.setNotificationUri(_context!!.contentResolver, uri)
            }
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val returnUri: Uri
        val db = NotifierDataProvider.db!!.writableDatabase

        when (uriMatcher.match(uri)) {
            CONTEST_ENTRIES -> {
                val contestId = db.insert(NotifierContract.ContestEntry.TABLE_NAME, null, values)
                if (contestId > -1) {
                    returnUri = uri.buildUpon().appendPath(String.format("%s", contestId)).build()
                } else {
                    throw SQLException("Failed to insert a new contest into: $uri")
                }
            }
            CONTESTANT_ENTRIES -> {
                val contestantId = db.insert(NotifierContract.ContestantEntry.TABLE_NAME, null, values)
                if (contestantId > -1) {
                    returnUri = uri.buildUpon().appendPath(String.format("%s", contestantId)).build()
                } else {
                    throw SQLException("Failed to insert a new contestant into: $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown operation URI: $uri")
        }

        _context!!.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var deleted = 0
        val db = NotifierDataProvider.db!!.writableDatabase

        when (uriMatcher.match(uri)) {
            CONTEST_ENTRIES_BY_IDS -> {
                val inCondition = StringBuilder()

                if (selectionArgs != null && selectionArgs.isNotEmpty()) {
                    for (arg in selectionArgs) {
                        inCondition.append("'").append(arg).append("',")
                    }
                    inCondition.deleteCharAt(inCondition.lastIndexOf(","))

                    deleted = db.delete(NotifierContract.ContestEntry.TABLE_NAME,
                            NotifierContract.ContestEntry.COLUMN_CONTEST_ID + " IN (" + inCondition.toString() + ")", null)
                }
            }
            else -> throw UnsupportedOperationException("Unknown operation URI: $uri")
        }

        return deleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val updated: Int
        val db = NotifierDataProvider.db!!.writableDatabase

        updated = when (uriMatcher.match(uri)) {
            CONTEST_ENTRIES -> db.update(NotifierContract.ContestEntry.TABLE_NAME, values, selection, selectionArgs)
            CONTESTANT_ENTRIES -> db.update(NotifierContract.ContestantEntry.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown operation URI: $uri")
        }

        return updated
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {

        private var db: NotifierDbHelper? = null
        private val uriMatcher = buildUriMatcher()

        private const val CONTEST_ENTRIES = 100
        private const val CONTEST_ENTRIES_BY_IDS = 101

        private const val CONTESTANT_ENTRIES = 200
        private const val CONTESTANT_ENTRIES_BY_CONTEST_ID = 201

        private fun buildUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTS, CONTEST_ENTRIES)
            uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTS + "/byContestIds", CONTEST_ENTRIES_BY_IDS)

            uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTANTS, CONTESTANT_ENTRIES)
            uriMatcher.addURI(NotifierContract.AUTHORITY, NotifierContract.PATH_CONTESTANTS + "/byContestId/*", CONTESTANT_ENTRIES_BY_CONTEST_ID)

            return uriMatcher
        }
    }
}
