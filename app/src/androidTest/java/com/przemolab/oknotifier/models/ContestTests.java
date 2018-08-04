package com.przemolab.oknotifier.models;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContestTests {
    
    @Test
    public void createFromParcel_returnsParceledContest() {
        // given
        Contest contest = new Contest("abc", "name");

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
