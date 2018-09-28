package com.przemolab.oknotifier.data.daos

import android.arch.persistence.room.*
import com.przemolab.oknotifier.data.NotifierContract
import com.przemolab.oknotifier.data.entries.ContestEntry

@Dao
interface ContestDao {

    @Query("SELECT * FROM ${NotifierContract.ContestEntry.TABLE_NAME} ORDER BY ${NotifierContract.ContestEntry.COLUMN_NAME}")
    fun getAll(): List<ContestEntry>

    @Query("SELECT * FROM ${NotifierContract.ContestEntry.TABLE_NAME} WHERE ${NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED} = 1")
    fun getSubscribed(): List<ContestEntry>

    @Query("DELETE FROM ${NotifierContract.ContestEntry.TABLE_NAME}")
    fun deleteAll()

    @Delete
    fun deleteMany(contestEntries: List<ContestEntry>)

    @Insert
    fun insert(contestEntry: ContestEntry)

    @Insert
    fun insertMany(contestEntries: List<ContestEntry>)

    @Update
    fun update(contestEntry: ContestEntry)

    @Query("UPDATE ${NotifierContract.ContestEntry.TABLE_NAME} " +
            "SET ${NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS} = :numberOfContestants " +
            "WHERE ${NotifierContract.ContestEntry.COLUMN_CONTEST_ID} = :contestId")
    fun updateNumberOfContestants(contestId: String, numberOfContestants: Int)
}