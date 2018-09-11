package com.przemolab.oknotifier.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    const val SQLiteDateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    const val DisplayFormat = "M/d/yy h:mma"

    fun addTimeToDate(date: Date, timeToAdd: String): Date {
        val timeParts = timeToAdd.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hours = Integer.parseInt(timeParts[0])
        val minutes = Integer.parseInt(timeParts[1])
        val seconds = Integer.parseInt(timeParts[2])

        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.HOUR, hours)
        cal.add(Calendar.MINUTE, minutes)
        cal.add(Calendar.SECOND, seconds)
        return cal.time
    }

    fun getDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, seconds)
        return cal.time
    }

    @Throws(ParseException::class)
    fun getDate(dateString: String, dateFormat: String): Date {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
        return simpleDateFormat.parse(dateString)
    }

    fun formatDate(date: Date, dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)
        return simpleDateFormat.format(date)
    }
}
