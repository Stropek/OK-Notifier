package com.przemolab.oknotifier.data

import android.net.Uri
import android.provider.BaseColumns

object NotifierContract {
    const val AUTHORITY = "com.przemolab.oknotifier"

    const val PATH_CONTESTS = "contests"
    const val PATH_CONTESTANTS = "contestants"

    var BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")!!

    class ContestEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTESTS).build()!!

            const val TABLE_NAME = "contests"

            const val _ID = "_id"
            const val COLUMN_CONTEST_ID = "contest_id"
            const val COLUMN_CREATED_DATE = "created_date"
            const val COLUMN_LAST_MODIFIED_DATE = "last_modified_date"
            const val COLUMN_NAME = "name"
            const val COLUMN_START_DATE = "start_date"
            const val COLUMN_END_DATE = "end_date"
            const val COLUMN_NUM_OF_CONTESTANTS = "number_of_contestants"
            const val COLUMN_NUM_OF_PROBLEMS = "number_of_problems"
            const val COLUMN_IS_SUBSCRIBED = "is_subscribed"
        }
    }

    class ContestantEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTESTANTS).build()!!

            const val TABLE_NAME = "contestants"

            const val _ID = "_id"
            const val COLUMN_NAME = "name"
            const val COLUMN_CONTEST_ID = "contest_id"
            const val COLUMN_CREATED_DATE = "created_date"
            const val COLUMN_LAST_MODIFIED_DATE = "last_modified_date"
            // TODO: change solved -> approved
            const val COLUMN_PROBLEMS_SOLVED = "problems_solved"
            const val COLUMN_PROBLEMS_SUBMITTED = "problems_submitted"
            // TODO: change failed -> rejected
            const val COLUMN_PROBLEMS_FAILED = "problems_failed"
            const val COLUMN_PROBLEMS_NOT_TRIED = "problems_not_tried"
            const val COLUMN_TIME = "time"
        }
    }
}
