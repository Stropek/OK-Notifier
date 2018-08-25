package com.przemolab.oknotifier.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.przemolab.oknotifier.utils.DateUtils;
import com.przemolab.oknotifier.data.NotifierContract;

import java.text.ParseException;
import java.util.Date;

public class Contest implements Parcelable {

    private int id;
    private String contestId;
    private String name;
    private Date startDate;
    private Date endDate;
    private int numberOfContestants;
    private int numberOfProblems;
    private boolean isSubscribed = false;

    private Contest() { }

    public Contest(String contestId, String name, Date startDate, Date endDate, int numberOfContestants, int numberOfProblems) {
        this.id = 0;
        this.contestId = contestId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfContestants = numberOfContestants;
        this.numberOfProblems = numberOfProblems;
    }

    private Contest(Parcel in) {
        this.id = in.readInt();
        this.contestId = in.readString();
        this.name = in.readString();
        this.startDate = new Date(in.readLong());
        this.endDate = new Date(in.readLong());
        this.numberOfContestants = in.readInt();
        this.numberOfProblems = in.readInt();
        this.isSubscribed = in.readByte() == 1;
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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(NotifierContract.ContestEntry.COLUMN_NAME, this.name);
        values.put(NotifierContract.ContestEntry.COLUMN_CONTEST_ID, this.contestId);
        values.put(NotifierContract.ContestEntry.COLUMN_START_DATE, DateUtils.formatDate(this.startDate, DateUtils.SQLiteDateTimeFormat));
        values.put(NotifierContract.ContestEntry.COLUMN_END_DATE, DateUtils.formatDate(this.endDate, DateUtils.SQLiteDateTimeFormat));
        values.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS, this.numberOfContestants);
        values.put(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS, this.numberOfProblems);
        values.put(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED, this.isSubscribed);

        return values;
    }

    public static Contest getFromCursor(Cursor cursor) throws ParseException {
        Contest contest = new Contest();

        contest.setId(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry._ID)));
        contest.setContestId(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_CONTEST_ID)));
        contest.setName(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NAME)));
        contest.setStartDate(DateUtils.getDate(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_START_DATE)), DateUtils.SQLiteDateTimeFormat));
        contest.setEndDate(DateUtils.getDate(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_END_DATE)), DateUtils.SQLiteDateTimeFormat));
        contest.setNumberOfContestants(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS)));
        contest.setNumberOfProblems(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS)));
        contest.setSubscribed(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED)) == 1);

        return contest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contestId);
        dest.writeString(name);
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
        dest.writeInt(numberOfContestants);
        dest.writeInt(numberOfProblems);
        dest.writeByte((byte) (isSubscribed ? 1 : 0));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
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
        return DateUtils.formatDate(startDate, DateUtils.DisplayFormat);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateFormatted() {
        return DateUtils.formatDate(endDate, DateUtils.DisplayFormat);
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
