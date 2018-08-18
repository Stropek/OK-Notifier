package com.przemolab.oknotifier.modules;

import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;

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

    @Test
    public void getContestStandings_returnsContestantsList() {
        // given
        OpenKattisService service = new OpenKattisService();
        List<Contest> contests = service.getOngoingContests();

        if (!contests.isEmpty()) {
            // when
            List<Contestant> result = service.getContestStandings(contests.get(0).getId());

            // then
            assertNotNull(result);
        }
    }
}
