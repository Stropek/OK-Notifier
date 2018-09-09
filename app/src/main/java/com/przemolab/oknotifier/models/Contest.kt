package com.przemolab.oknotifier.models

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

import com.przemolab.oknotifier.utils.DateUtils
import com.przemolab.oknotifier.data.NotifierContract

import java.text.ParseException
import java.util.Date

class Contest : Parcelable {

    var id: Int = 0
    var contestId: String = ""
    var name: String = ""
    lateinit var startDate: Date
    lateinit var endDate: Date
    var numberOfContestants: Int = 0
    var numberOfProblems: Int = 0
    var isSubscribed = false

    val startDateFormatted: String
        get() = DateUtils.formatDate(startDate!!, DateUtils.DisplayFormat)

    val endDateFormatted: String
        get() = DateUtils.formatDate(endDate!!, DateUtils.DisplayFormat)

    private constructor()

    constructor(contestId: String, name: String, startDate: Date, endDate: Date, numberOfContestants: Int, numberOfProblems: Int) {
        this.id = 0
        this.contestId = contestId
        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.numberOfContestants = numberOfContestants
        this.numberOfProblems = numberOfProblems
    }

    private constructor(`in`: Parcel) {
        this.id = `in`.readInt()
        this.contestId = `in`.readString()
        this.name = `in`.readString()
        this.startDate = Date(`in`.readLong())
        this.endDate = Date(`in`.readLong())
        this.numberOfContestants = `in`.readInt()
        this.numberOfProblems = `in`.readInt()
        this.isSubscribed = `in`.readByte().toInt() == 1
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues()

        values.put(NotifierContract.ContestEntry.COLUMN_NAME, this.name)
        values.put(NotifierContract.ContestEntry.COLUMN_CONTEST_ID, this.contestId)
        values.put(NotifierContract.ContestEntry.COLUMN_START_DATE, DateUtils.formatDate(this.startDate!!, DateUtils.SQLiteDateTimeFormat))
        values.put(NotifierContract.ContestEntry.COLUMN_END_DATE, DateUtils.formatDate(this.endDate!!, DateUtils.SQLiteDateTimeFormat))
        values.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, this.numberOfContestants)
        values.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, this.numberOfProblems)
        values.put(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED, this.isSubscribed)

        return values
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(contestId)
        dest.writeString(name)
        dest.writeLong(startDate!!.time)
        dest.writeLong(endDate!!.time)
        dest.writeInt(numberOfContestants)
        dest.writeInt(numberOfProblems)
        dest.writeByte((if (isSubscribed) 1 else 0).toByte())
    }

    companion object {

        val CREATOR: Parcelable.Creator<Contest> = object : Parcelable.Creator<Contest> {
            override fun createFromParcel(`in`: Parcel): Contest {
                return Contest(`in`)
            }

            override fun newArray(size: Int): Array<Contest?> {
                return arrayOfNulls(size)
            }
        }

        @Throws(ParseException::class)
        fun getFromCursor(cursor: Cursor): Contest {
            val contest = Contest()

            contest.id = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry._ID))
            contest.contestId = cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID))
            contest.name = cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME))
            contest.startDate = DateUtils.getDate(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_START_DATE)), DateUtils.SQLiteDateTimeFormat)
            contest.endDate = DateUtils.getDate(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_END_DATE)), DateUtils.SQLiteDateTimeFormat)
            contest.numberOfContestants = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS))
            contest.numberOfProblems = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS))
            contest.isSubscribed = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED)) == 1

            return contest
        }
    }
}
