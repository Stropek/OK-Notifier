package com.przemolab.oknotifier.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.przemolab.oknotifier.utils.DateUtils;
import com.przemolab.oknotifier.data.ContestContract;

import java.text.ParseException;
import java.util.Date;

public class Contest implements Parcelable {

    private String id;
    private String name;
    private Date startDate;
    private Date endDate;
    private int numberOfContestants;
    private int numberOfProblems;
    private boolean isSubscribed = false;

    private Contest() { }

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

    public static Contest getFromCursor(Cursor cursor) throws ParseException {
        Contest contest = new Contest();

        contest.setId(cursor.getString(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_CONTEST_ID)));
        contest.setName(cursor.getString(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_NAME)));
        contest.setStartDate(DateUtils.getDate(cursor.getString(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_START_DATE))));
        contest.setEndDate(DateUtils.getDate(cursor.getString(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_END_DATE))));
        contest.setNumberOfContestants(cursor.getInt(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS)));
        contest.setNumberOfProblems(cursor.getInt(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS)));
        contest.setSubscribed(cursor.getInt(cursor.getColumnIndex(ContestContract.ContestEntry.COLUMN_IS_SUBSCRIBED)) == 1);

        return contest;
    }

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

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.isSubscribed = subscribed;
    }
}
