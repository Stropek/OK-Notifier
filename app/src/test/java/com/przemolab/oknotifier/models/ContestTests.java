package com.przemolab.oknotifier.models;

import com.przemolab.oknotifier.utils.DateUtils;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class ContestTests {

    @Test
    public void constructor_setsAllFields() {
        // when
        Contest contest = createContest();

        // then
        assertEquals("abc", contest.getId());
        assertEquals("name", contest.getName());
    }

    @Test
    public void setId_setsId() {
        // given
        Contest contest = createContest();

        // when
        contest.setId("changed id");

        // then
        assertEquals("changed id", contest.getId());
    }

    @Test
    public void setName_setsName() {
        // given
        Contest contest = createContest();

        // when
        contest.setName("changed name");

        // then
        assertEquals("changed name", contest.getName());
    }

    @Test
    public void setStartDate_setsStartDate() {
        // given
        Contest contest = createContest();
        Date newDate = DateUtils.getDate(2000, 1, 1, 10, 30);

        // when
        contest.setStartDate(newDate);

        // then
        assertEquals(newDate, contest.getStartDate());
    }

    @Test
    public void setEndDate_setsEndDate() {
        // given
        Contest contest = createContest();
        Date newDate = DateUtils.getDate(2000, 1, 1, 10, 30);

        // when
        contest.setEndDate(newDate);

        // then
        assertEquals(newDate, contest.getEndDate());
    }

    @Test
    public void setNumberOfContestants_setsNumberOfContestants() {
        // given
        Contest contest = createContest();

        // when
        contest.setNumberOfContestants(100);

        // then
        assertEquals(100, contest.getNumberOfContestants());
    }

    @Test
    public void setNumberOfProblems_setsNumberOfProblems() {
        // given
        Contest contest = createContest();

        // when
        contest.setNumberOfProblems(100);

        // then
        assertEquals(100, contest.getNumberOfProblems());
    }

    private Contest createContest() {
        String id = "abc";
        String name = "name";
        Date startDate = DateUtils.getDate(2015, 6, 10, 16, 0);
        Date endDate = DateUtils.getDate(2015, 6, 15, 16, 0);
        int numberOfContestants = 5;
        int numberOfProblems = 10;

        return new Contest(id, name, startDate, endDate, numberOfContestants, numberOfProblems);
    }
}