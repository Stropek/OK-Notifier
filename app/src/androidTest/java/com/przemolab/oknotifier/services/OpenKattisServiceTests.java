package com.przemolab.oknotifier.services;

import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.models.Contest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class OpenKattisServiceTests {

    @Test
    public void getOngoingContests_returnsContestsList() {
        // given
        OpenKattisService service = new OpenKattisService();

        // when
        List<Contest> result = service.getOngoingContests();

        // then
        assertNotNull(result);
    }
}
