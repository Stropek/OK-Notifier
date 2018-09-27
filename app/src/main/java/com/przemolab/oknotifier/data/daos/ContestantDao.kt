package com.przemolab.oknotifier.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.data.entries.ContestantEntry

@Dao
interface ContestantDao {

    @Query("SELECT * FROM ${NotifierContract.ContestantEntry.TABLE_NAME} " +
            "WHERE ${NotifierContract.ContestantEntry.COLUMN_CONTEST_ID} = :contestId " +
            "ORDER BY ${NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED} DESC, ${NotifierContract.ContestantEntry.COLUMN_TIME}")
    fun getByContestId(contestId: String): List<ContestantEntry>

    @Insert
    fun insert(contestantEntry: ContestantEntry)

    @Update
    fun update(contestantEntry: ContestantEntry)

    @Query("DELETE FROM ${NotifierContract.ContestantEntry.TABLE_NAME}")
    fun deleteAll()
}