package com.przemolab.oknotifier.models

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

import com.przemolab.oknotifier.data.NotifierContract

class Contestant : Parcelable {

    var id: Int = 0
    var name: String? = null
    var contestId: String = ""
    var problemsSolved: Int = 0
    var problemsSubmitted: Int = 0
    var problemsFailed: Int = 0
    var problemsNotTried: Int = 0
    var time: Int = 0

    val sharedPreferencesValue: String
        get() = String.format("%s;%s;%s;%s;%s", contestId, problemsSolved, problemsSubmitted, problemsFailed, problemsNotTried)

    private constructor()

    constructor(name: String, contestId: String, problemsSolved: Int, problemsSubmitted: Int, problemsFailed: Int, problemsNotTried: Int, time: Int) : this(0, name, contestId, problemsSolved, problemsSubmitted, problemsFailed, problemsNotTried, time)

    constructor(id: Int, name: String, contestId: String, problemsSolved: Int, problemsSubmitted: Int, problemsFailed: Int, problemsNotTried: Int, time: Int) {
        this.id = id
        this.name = name
        this.contestId = contestId
        this.problemsSolved = problemsSolved
        this.problemsSubmitted = problemsSubmitted
        this.problemsFailed = problemsFailed
        this.problemsNotTried = problemsNotTried
        this.time = time
    }

    constructor(`in`: Parcel) {
        id = `in`.readInt()
        name = `in`.readString()
        contestId = `in`.readString()!!
        problemsSolved = `in`.readInt()
        problemsSubmitted = `in`.readInt()
        problemsFailed = `in`.readInt()
        problemsNotTried = `in`.readInt()
        time = `in`.readInt()
    }

    fun toContentValues(): ContentValues {
        val values = ContentValues()

        values.put(NotifierContract.ContestantEntry.COLUMN_NAME, this.name)
        values.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, this.contestId)
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, this.problemsSolved)
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, this.problemsSubmitted)
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, this.problemsFailed)
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, this.problemsNotTried)
        values.put(NotifierContract.ContestantEntry.COLUMN_TIME, this.time)

        return values
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeString(contestId)
        dest.writeInt(problemsSolved)
        dest.writeInt(problemsSubmitted)
        dest.writeInt(problemsFailed)
        dest.writeInt(problemsNotTried)
        dest.writeInt(time)
    }

    companion object {

        val CREATOR: Parcelable.Creator<Contestant> = object : Parcelable.Creator<Contestant> {
            override fun createFromParcel(`in`: Parcel): Contestant {
                return Contestant(`in`)
            }

            override fun newArray(size: Int): Array<Contestant?> {
                return arrayOfNulls(size)
            }
        }

        fun getFromCursor(cursor: Cursor): Contestant {
            val contestant = Contestant()

            contestant.id = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry._ID))
            contestant.name = cursor.getString(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_NAME))
            contestant.contestId = cursor.getString(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID))
            contestant.problemsSolved = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED))
            contestant.problemsSubmitted = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED))
            contestant.problemsFailed = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED))
            contestant.problemsNotTried = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED))
            contestant.time = cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_TIME))

            return contestant
        }
    }
}
