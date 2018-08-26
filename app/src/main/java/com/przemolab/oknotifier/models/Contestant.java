package com.przemolab.oknotifier.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.przemolab.oknotifier.data.NotifierContract;

public class Contestant implements Parcelable {

    private int id;
    private String name;
    private String contestId;
    private int problemsSolved;
    private int problemsSubmitted;
    private int problemsFailed;
    private int problemsNotTried;
    private int time;

    private Contestant() {}

    public Contestant(String name, String contestId, int problemsSolved, int problemsSubmitted, int problemsFailed, int problemsNotTried, int time) {
        this(0, name, contestId, problemsSolved, problemsSubmitted, problemsFailed, problemsNotTried, time);
    }

    public Contestant(int id, String name, String contestId, int problemsSolved, int problemsSubmitted, int problemsFailed, int problemsNotTried, int time) {
        this.id = id;
        this.name = name;
        this.contestId = contestId;
        this.problemsSolved = problemsSolved;
        this.problemsSubmitted = problemsSubmitted;
        this.problemsFailed = problemsFailed;
        this.problemsNotTried = problemsNotTried;
        this.time = time;
    }

    public Contestant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        contestId = in.readString();
        problemsSolved = in.readInt();
        problemsSubmitted = in.readInt();
        problemsFailed = in.readInt();
        problemsNotTried = in.readInt();
        time = in.readInt();
    }

    public static final Creator<Contestant> CREATOR = new Creator<Contestant>() {
        @Override
        public Contestant createFromParcel(Parcel in) {
            return new Contestant(in);
        }

        @Override
        public Contestant[] newArray(int size) {
            return new Contestant[size];
        }
    };

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(NotifierContract.ContestantEntry.COLUMN_NAME, this.name);
        values.put(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID, this.contestId);
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED, this.problemsSolved);
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED, this.problemsSubmitted);
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED, this.problemsFailed);
        values.put(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED, this.problemsNotTried);
        values.put(NotifierContract.ContestantEntry.COLUMN_TIME, this.time);

        return values;
    }

    public static Contestant getFromCursor(Cursor cursor) {
        Contestant contestant = new Contestant();

        contestant.setId(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry._ID)));
        contestant.setName(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_NAME)));
        contestant.setContestId(cursor.getString(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_CONTEST_ID)));
        contestant.setProblemsSolved(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SOLVED)));
        contestant.setProblemsSubmitted(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_SUBMITTED)));
        contestant.setProblemsFailed(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_FAILED)));
        contestant.setProblemsNotTried(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_PROBLEMS_NOT_TRIED)));
        contestant.setTime(cursor.getInt(cursor.getColumnIndex(NotifierContract.ContestantEntry.COLUMN_TIME)));

        return contestant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(contestId);
        dest.writeInt(problemsSolved);
        dest.writeInt(problemsSubmitted);
        dest.writeInt(problemsFailed);
        dest.writeInt(problemsNotTried);
        dest.writeInt(time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public int getProblemsSolved() {
        return problemsSolved;
    }

    public void setProblemsSolved(int problemsSolved) {
        this.problemsSolved = problemsSolved;
    }

    public int getProblemsSubmitted() {
        return problemsSubmitted;
    }

    public void setProblemsSubmitted(int problemsSubmitted) {
        this.problemsSubmitted = problemsSubmitted;
    }

    public int getProblemsFailed() {
        return problemsFailed;
    }

    public void setProblemsFailed(int problemsFailed) {
        this.problemsFailed = problemsFailed;
    }

    public int getProblemsNotTried() {
        return problemsNotTried;
    }

    public void setProblemsNotTried(int problemsNotTried) {
        this.problemsNotTried = problemsNotTried;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSharedPreferencesValue() {
        return String.format("%s;%s;%s;%s;%s", contestId, problemsSolved, problemsSubmitted, problemsFailed, problemsNotTried);
    }
}
