package com.przemolab.oknotifier.utils;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class DateUtilsTests {

    @Test
    public void formatDate_validDateObject_returnsDateString() {
        // given
        Date date = DateUtils.getDate(2014, 8, 1, 14, 8);

        // when
        String result = DateUtils.formatDate(date);

        // then
        assertEquals("8/1/14 2:08PM", result);
    }
}
