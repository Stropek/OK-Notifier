package com.przemolab.oknotifier.utils

import org.junit.Test

import java.text.ParseException
import java.util.Calendar
import java.util.Date

import junit.framework.Assert.assertEquals

class DateUtilsTests {

    @Test
    @Throws(ParseException::class)
    fun getDate_dateTimeFormatString_returnsDate() {
        // given
        val dateTime = "2018-07-30 18:45:30"

        // when
        val result = DateUtils.getDate(dateTime, "yyyy-MM-dd HH:mm:ss")

        // then
        val cal = Calendar.getInstance()
        cal.time = result
        assertEquals(2018, cal.get(Calendar.YEAR))
        assertEquals(7, cal.get(Calendar.MONTH) + 1)
        assertEquals(30, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(45, cal.get(Calendar.MINUTE))
        assertEquals(30, cal.get(Calendar.SECOND))
    }

    @Test
    @Throws(ParseException::class)
    fun getDate_timeFormatString_returnsDate() {
        // given
        val dateTime = "18:45:30"

        // when
        val result = DateUtils.getDate(dateTime, "HH:mm:ss")

        // then
        val cal = Calendar.getInstance()
        cal.time = result
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(45, cal.get(Calendar.MINUTE))
        assertEquals(30, cal.get(Calendar.SECOND))
    }

    @Test(expected = ParseException::class)
    @Throws(ParseException::class)
    fun getDate_invalidFormat_throwsParseException() {
        // given
        val dateTime = "INVALID_DATE"

        // when
        DateUtils.getDate(dateTime, "")
    }

    @Test
    fun addTimeToDate_validDateAndTimeToAdd_returnsAdjustedDate() {
        // given
        val date = DateUtils.getDate(2014, 12, 30, 12, 15, 10)
        val timeToAdd = "50:25:15"

        // when
        val result = DateUtils.addTimeToDate(date, timeToAdd)

        // then
        val cal = Calendar.getInstance()
        cal.time = result
        assertEquals(2015, cal.get(Calendar.YEAR))
        assertEquals(1, cal.get(Calendar.MONTH) + 1)
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(40, cal.get(Calendar.MINUTE))
        assertEquals(25, cal.get(Calendar.SECOND))
    }

    @Test
    fun formatDate_validDateObject_returnsDateString() {
        // given
        val date = DateUtils.getDate(2014, 8, 1, 14, 8, 0)

        // when
        val result = DateUtils.formatDate(date, DateUtils.DisplayFormat)

        // then
        assertEquals("8/1/14 2:08PM", result)
    }
}
