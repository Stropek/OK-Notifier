package com.przemolab.oknotifier.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import java.util.Date

@Entity(tableName = NotifierContract.ContestEntry.TABLE_NAME)
class ContestEntry {

    @PrimaryKey
    var id: Int = 0

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_CONTEST_ID)
    var contestId: String? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_CREATED_DATE)
    var createdDate: Date? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_LAST_MODIFIED_DATE)
    var lastModifiedDate: Date? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_NAME)
    var name: String? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_START_DATE)
    var startDate: Date? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_END_DATE)
    var endDate: Date? = null

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS)
    var numberOfContestants: Int = 0

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS)
    var numberOfProblems: Int = 0

    @ColumnInfo(name = NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED)
    var subscribed: Boolean = false
}