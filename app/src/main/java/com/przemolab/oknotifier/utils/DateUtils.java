package com.przemolab.oknotifier.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static Date getDate(int year, int month, int day, int hour, int minute, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    public static Date getDate(String dateString) throws ParseException {
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String timeFormat = "HH:mm:ss";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat, Locale.US);
        if (dateString.length() == 8) {
            simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.US);
        }

        return simpleDateFormat.parse(dateString);
    }

    public static Date addTimeToDate(Date date, String timeToAdd) {
        String[] timeParts = timeToAdd.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    public static String formatDate(Date date) {
        String dateFormat = "M/d/yy h:mma";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }
}
