package com.przemolab.oknotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

public class Contest implements Parcelable {

    private String id;
    private String name;

    public static List<Contest> DUMMY_ITEMS = Arrays.asList(
            new Contest("abc", "Contest 1"),
            new Contest("def", "Contest 2"),
            new Contest("ghi", "Contest 3"),
            new Contest("jkl", "Contest 4"),
            new Contest("mno", "Contest 5"),
            new Contest("prs", "Contest 6")
    );

    public Contest(String id, String name) {
        this.id = id;
        this.name = name;
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
}
