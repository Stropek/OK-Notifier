package com.przemolab.oknotifier.data

import android.arch.persistence.room.*
import android.content.Context
import com.przemolab.oknotifier.data.converters.DateConverter
import com.przemolab.oknotifier.data.daos.ContestDao
import com.przemolab.oknotifier.data.daos.ContestantDao
import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.data.entries.ContestantEntry
import timber.log.Timber

@Database(entities = [ContestEntry::class, ContestantEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        private const val DbName: String = "ok-notifier-db"

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        Timber.d("Creating new database instance")
                        instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DbName)
                                .build()
                    }
                }
            }
            return instance
        }
    }

    abstract fun contestDao(): ContestDao

    abstract fun contestantDao(): ContestantDao
}