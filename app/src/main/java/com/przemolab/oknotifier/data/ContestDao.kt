package com.przemolab.oknotifier.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface ContestDao {

    @Query("SELECT * FROM ${NotifierContract.ContestEntry.TABLE_NAME} ORDER BY ${NotifierContract.ContestEntry.COLUMN_NAME}")
    fun getAll(): List<ContestEntry>

    @Query("DELETE FROM ${NotifierContract.ContestEntry.TABLE_NAME}")
    fun deleteAll()

    @Insert
    fun insertAll(contest: List<ContestEntry>)
}