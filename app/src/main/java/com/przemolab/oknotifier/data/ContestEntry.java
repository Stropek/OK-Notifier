package com.przemolab.oknotifier.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity (tableName = "contests")
public class ContestEntry {

    @PrimaryKey
    private int id;
    @ColumnInfo (name = "contest_id")
    private String contestId;
    @ColumnInfo (name = "created_date")
    private Date createdDate;
    @ColumnInfo (name = "last_modified_date")
    private Date lastModifiedDate;
    private String name;
    @ColumnInfo (name = "start_date")
    private Date startDate;
    @ColumnInfo (name = "end_date")
    private Date endDate;
    @ColumnInfo (name = "number_of_contestants")
    private int numberOfContestants;
    @ColumnInfo (name = "number_of_problems")
    private int numberOfProblems;
    @ColumnInfo (name = "is_subscribed")
    private int isSubscribed;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
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

    public int getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(int isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
}
