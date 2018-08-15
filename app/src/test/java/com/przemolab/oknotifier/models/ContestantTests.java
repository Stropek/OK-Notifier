package com.przemolab.oknotifier.models;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ContestantTests {
    
    @Test
    public void constructor_setsAllFields() {
        // when
        Contestant contestant = createContestant();

        // then
        assertEquals("1", contestant.getId());
        assertEquals("abc", contestant.getContestId());
        assertEquals(1, contestant.getProblemsSolved());
        assertEquals(2, contestant.getProblemsSubmitted());
        assertEquals(3, contestant.getProblemsFailed());
        assertEquals(4, contestant.getProblemsNotTried());
        assertEquals(5, contestant.getTime());
    }

    @Test
    public void setId_setsId() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setId("changed id");

        // then
        assertEquals("changed id", contestant.getId());
    }

    @Test
    public void setContestId_setsContestId() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setContestId("changed contest id");

        // then
        assertEquals("changed contest id", contestant.getContestId());
    }

    @Test
    public void setProblemsSolved_setsProblemsSolved() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setProblemsSolved(60);

        // then
        assertEquals(60, contestant.getProblemsSolved());
    }

    @Test
    public void setProblemsSubmitted_setsProblemsSubmitted() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setProblemsSubmitted(40);

        // then
        assertEquals(40, contestant.getProblemsSubmitted());
    }

    @Test
    public void setProblemsFailed_setsProblemsFailed() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setProblemsFailed(20);

        // then
        assertEquals(20, contestant.getProblemsFailed());
    }

    @Test
    public void setProblemsNotTried_setsProblemsNotTried() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setProblemsNotTried(30);

        // then
        assertEquals(30, contestant.getProblemsNotTried());
    }

    @Test
    public void setTime_setsTime() {
        // given
        Contestant contestant = createContestant();

        // when
        contestant.setTime(10);

        // then
        assertEquals(10, contestant.getTime());
    }

    private Contestant createContestant() {
        String id = "1";
        String contestId = "abc";
        int solved = 1;
        int submitted = 2;
        int failed = 3;
        int notTried = 4;
        int time = 5;

        return new Contestant(id, contestId, solved, submitted, failed, notTried, time);
    }
}
