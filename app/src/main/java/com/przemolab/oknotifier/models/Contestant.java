package com.przemolab.oknotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contestant implements Parcelable {

    private String id;
    private String contestId;
    private int problemsSolved;
    private int problemsSubmitted;
    private int problemsFailed;
    private int problemsNotTried;
    private int time;

    private Contestant() {}

    public Contestant(String id, String contestId, int problemsSolved, int problemsSubmitted, int problemsFailed, int problemsNotTried, int time) {
        this.id = id;
        this.contestId = contestId;
        this.problemsSolved = problemsSolved;
        this.problemsSubmitted = problemsSubmitted;
        this.problemsFailed = problemsFailed;
        this.problemsNotTried = problemsNotTried;
        this.time = time;
    }

    public Contestant(Parcel in) {
        id = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(contestId);
        dest.writeInt(problemsSolved);
        dest.writeInt(problemsSubmitted);
        dest.writeInt(problemsFailed);
        dest.writeInt(problemsNotTried);
        dest.writeInt(time);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
