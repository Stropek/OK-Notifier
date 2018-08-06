package com.przemolab.oknotifier.models;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.utils.DateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContestTests {
    
    @Test
    public void createFromParcel_returnsParceledContest() {
        // given
        Date startDate = DateUtils.getDate(2015, 6, 10, 16, 0, 0);
        Date endDate = DateUtils.getDate(2015, 6, 15, 16, 0, 0);
        Contest contest = new Contest("abc", "name", startDate, endDate, 5, 10);

        // when
        Parcel parcel = Parcel.obtain();
        contest.writeToParcel(parcel, contest.describeContents());
        parcel.setDataPosition(0);
        Contest fromParcel = Contest.CREATOR.createFromParcel(parcel);

        // then
        assertEquals(fromParcel.getId(), contest.getId());
        assertEquals(fromParcel.getName(), contest.getName());
    }

    @Test
    public void newArray_returnsNewArrayOfGivenSize() {
        // when
        Contest[] contests = Contest.CREATOR.newArray(10);

        // then
        assertEquals(10, contests.length);
    }
}
