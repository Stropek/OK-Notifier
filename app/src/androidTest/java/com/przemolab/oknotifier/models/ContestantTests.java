package com.przemolab.oknotifier.models;

import android.content.Context;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContestantTests {

    private static final Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void createFromParcel_returnsParceledContestant() {
        // given
        Contestant contestant = new Contestant(1, "abc", 1, 2, 3, 4 ,5);

        // when
        Parcel parcel = Parcel.obtain();
        contestant.writeToParcel(parcel, contestant.describeContents());
        parcel.setDataPosition(0);
        Contestant fromParcel = Contestant.CREATOR.createFromParcel(parcel);

        // then
        assertEquals(fromParcel.getId(), contestant.getId());
        assertEquals(fromParcel.getContestId(), contestant.getContestId());
        assertEquals(fromParcel.getProblemsSolved(), contestant.getProblemsSolved());
        assertEquals(fromParcel.getProblemsSubmitted(), contestant.getProblemsSubmitted());
        assertEquals(fromParcel.getProblemsFailed(), contestant.getProblemsFailed());
        assertEquals(fromParcel.getProblemsNotTried(), contestant.getProblemsNotTried());
        assertEquals(fromParcel.getTime(), contestant.getTime());
    }

    @Test
    public void newArray_returnsNewArrayOfGivenSize() {
        // when
        Contestant[] contestants = Contestant.CREATOR.newArray(10);

        // then
        assertEquals(10, contestants.length);
    }
}
