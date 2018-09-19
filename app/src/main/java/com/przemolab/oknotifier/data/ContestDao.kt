package com.przemolab.oknotifier.data

import android.arch.persistence.room.*

@Dao
interface ContestDao {

    @Query("SELECT * FROM ${NotifierContract.ContestEntry.TABLE_NAME} ORDER BY ${NotifierContract.ContestEntry.COLUMN_NAME}")
    fun getAll(): List<ContestEntry>

    @Query("DELETE FROM ${NotifierContract.ContestEntry.TABLE_NAME}")
    fun deleteAll()

    @Delete
    fun delete(contestEntry: ContestEntry)

    @Delete
    fun deleteMany(contestEntries: List<ContestEntry>)

    @Insert
    fun insert(contestEntry: ContestEntry)

    @Insert
    fun insertMany(contestEntries: List<ContestEntry>)

    @Update
    fun update(contestEntry: ContestEntry)

    @Update
    fun updateMany(contestEntries: List<ContestEntry>)
}