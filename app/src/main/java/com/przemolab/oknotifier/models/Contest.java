package com.przemolab.oknotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.przemolab.oknotifier.utils.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Contest implements Parcelable {

    private String id;
    private String name;
    private Date startDate;
    private Date endDate;
    private int numberOfContestants;
    private int numberOfProblems;

    // TODO: add isFollowed flag when persisting to database
    //    private boolean _isFollowed;

    public static List<Contest> DUMMY_ITEMS = Arrays.asList(
            new Contest("abc", "Contest 1", DateUtils.getDate(2000,1,11, 16, 0), DateUtils.getDate(2000, 1, 11, 17,30), 1, 10),
            new Contest("def", "Contest 2", DateUtils.getDate(2001,2,12, 16, 0), DateUtils.getDate(2001, 2, 12, 17,30), 2, 11),
            new Contest("ghi", "Contest 3", DateUtils.getDate(2002,3,13, 16, 0), DateUtils.getDate(2002, 3, 13, 17,30), 3, 12),
            new Contest("jkl", "Contest 4", DateUtils.getDate(2003,4,14, 16, 0), DateUtils.getDate(2003, 4, 14, 17,30), 4, 13),
            new Contest("mno", "Contest 5", DateUtils.getDate(2004,5,15, 16, 0), DateUtils.getDate(2004, 5, 15, 17,30), 5, 14),
            new Contest("prs", "Contest 6", DateUtils.getDate(2005,6,16, 16, 0), DateUtils.getDate(2005, 6, 16, 17,30), 6, 15)
    );

    public Contest(String id, String name, Date startDate, Date endDate, int numberOfContestants, int numberOfProblems) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfContestants = numberOfContestants;
        this.numberOfProblems = numberOfProblems;
    }

    private Contest(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Contest> CREATOR = new Creator<Contest>() {
        @Override
        public Contest createFromParcel(Parcel in) {
            return new Contest(in);
        }

        @Override
        public Contest[] newArray(int size) {
            return new Contest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateFormatted() {
        return DateUtils.formatDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateFormatted() {
        return DateUtils.formatDate(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfContestants() {
        return numberOfContestants;
    }

    public void setNumberOfContestants(int numberOfContestants) {
        this.numberOfContestants = numberOfContestants;
    }

    public int getNumberOfProblems() {
        return numberOfProblems;
    }

    public void setNumberOfProblems(int numberOfProblems) {
        this.numberOfProblems = numberOfProblems;
    }
}
