package com.przemolab.oknotifier.models;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ContestTests {

    @Test
    public void constructor_setsAllFields() {
        // when
        Contest contest = new Contest("abc", "name");

        // then
        assertEquals("abc", contest.getId());
        assertEquals("name", contest.getName());
    }

    @Test
    public void setId_setsId() {
        // given
        Contest contest = new Contest("abc", "name");

        // when
        contest.setId("def");

        // then
        assertEquals("def", contest.getId());
    }

    @Test
    public void setName_setsName() {
        // given
        Contest contest = new Contest("abc", "name");

        // when
        contest.setName("changed name");

        // then
        assertEquals("changed name", contest.getName());
    }
}