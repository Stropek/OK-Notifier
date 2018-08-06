package com.przemolab.oknotifier.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContestContract {
    public static final String AUTHORITY = "com.przemolab.oknotifier";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_CONTESTS = "contests";

    public static final class ContestEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTESTS).build();

        public static final String TABLE_NAME = "contests";

        public static final String COLUMN_CONTEST_ID = "contest_id";
        public static final String COLUMN_CREATED_DATE = "created_date";
        public static final String COLUMN_LAST_MODIFIED_DATE = "last_modified_date";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_NUM_OF_CONTESTANTS = "number_of_contestants";
        public static final String COLUMN_NUM_OF_PROBLEMS = "number_of_problems";
        public static final String COLUMN_IS_SUBSCRIBED = "is_subscribed";
    }
}
