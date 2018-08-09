package com.przemolab.oknotifier.utils;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class DateUtilsTests {

    @Test
    public void getDate_dateTimeFormatString_returnsDate() throws ParseException {
        // given
        String dateTime = "2018-07-30 18:45:30";

        // when
        Date result = DateUtils.getDate(dateTime, "yyyy-MM-dd HH:mm:ss");

        // then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2018, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH) + 1);
        assertEquals(30, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(30, cal.get(Calendar.SECOND));
    }

    @Test
    public void getDate_timeFormatString_returnsDate() throws ParseException {
        // given
        String dateTime = "18:45:30";

        // when
        Date result = DateUtils.getDate(dateTime, "HH:mm:ss");

        // then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(18, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(30, cal.get(Calendar.SECOND));
    }

    @Test(expected = ParseException.class)
    public void getDate_invalidFormat_throwsParseException() throws ParseException {
        // given
        String dateTime = "INVALID_DATE";

        // when
        DateUtils.getDate(dateTime, "");
    }

    @Test
    public void addTimeToDate_validDateAndTimeToAdd_returnsAdjustedDate() {
        // given
        Date date = DateUtils.getDate(2014, 12, 30, 12, 15, 10);
        String timeToAdd = "50:25:15";

        // when
        Date result = DateUtils.addTimeToDate(date, timeToAdd);

        // then
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2015, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1);
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(40, cal.get(Calendar.MINUTE));
        assertEquals(25, cal.get(Calendar.SECOND));
    }

    @Test
    public void formatDate_validDateObject_returnsDateString() {
        // given
        Date date = DateUtils.getDate(2014, 8, 1, 14, 8, 0);

        // when
        String result = DateUtils.formatDate(date, DateUtils.DisplayFormat);

        // then
        assertEquals("8/1/14 2:08PM", result);
    }
}
