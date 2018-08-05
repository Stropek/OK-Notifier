package com.przemolab.oknotifier.services;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.utils.DateUtils;

import java.util.Arrays;
import java.util.List;

public class OpenKattisService {

    public List<Contest> getOngoingContests() {
        return Arrays.asList(
                new Contest("abc", "From Mock Service 1", DateUtils.getDate(2000,1,11, 16, 0), DateUtils.getDate(2000, 1, 11, 17,30), 1, 10),
                new Contest("def", "From Mock Service 2", DateUtils.getDate(2001,2,12, 16, 0), DateUtils.getDate(2001, 2, 12, 17,30), 2, 11),
                new Contest("ghi", "From Mock Service 3", DateUtils.getDate(2002,3,13, 16, 0), DateUtils.getDate(2002, 3, 13, 17,30), 3, 12),
                new Contest("jkl", "From Mock Service 4", DateUtils.getDate(2003,4,14, 16, 0), DateUtils.getDate(2003, 4, 14, 17,30), 4, 13),
                new Contest("mno", "From Mock Service 5", DateUtils.getDate(2004,5,15, 16, 0), DateUtils.getDate(2004, 5, 15, 17,30), 5, 14),
                new Contest("prs", "From Mock Service 6", DateUtils.getDate(2005,6,16, 16, 0), DateUtils.getDate(2005, 6, 16, 17,30), 6, 15)
        );
    }
}

