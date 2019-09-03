package com.przemolab.oknotifier.data.converters

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {
    companion object {
        @TypeConverter @JvmStatic
        fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null)  null else Date(timestamp)
        }

        @TypeConverter @JvmStatic
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}