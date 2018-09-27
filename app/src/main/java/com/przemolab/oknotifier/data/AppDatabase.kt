package com.przemolab.oknotifier.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.przemolab.oknotifier.data.converters.DateConverter
import com.przemolab.oknotifier.data.daos.ContestDao
import com.przemolab.oknotifier.data.entries.ContestEntry
import timber.log.Timber

@Database(entities = [ContestEntry::class], version = 2, exportSchema = false)
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
                                .addMigrations(Migration1())
                                .build()
                    }
                }
            }
            return instance
        }

        class Migration1 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // TODO: nothing
            }
        }
    }

    abstract fun contestDao(): ContestDao
}