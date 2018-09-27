package com.przemolab.oknotifier.data.entries

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.przemolab.oknotifier.data.NotifierContract
import java.util.*

@Entity(tableName = NotifierContract.ContestantEntry.TABLE_NAME)
class ContestantEntry(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_CONTEST_ID) var contestId: String? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_CREATED_DATE) var createdDate: Date? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_LAST_MODIFIED_DATE) var lastModifiedDate: Date? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_NAME) var name: String? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED) var problemsSolved: Int? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED) var problemsSubmitted: Int? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED) var problemsFailed: Int? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED) var problemsNotTried: Int? = null,
        @ColumnInfo(name = NotifierContract.ContestantEntry.COLUMN_TIME) var time: Int? = null)
{
    @Ignore
    var sharedPreferencesValue: String = ""
        get() = String.format("%s;%s;%s;%s;%s", contestId, problemsSolved, problemsSubmitted, problemsFailed, problemsNotTried)
}