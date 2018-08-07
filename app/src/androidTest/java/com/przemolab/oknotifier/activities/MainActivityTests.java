package com.przemolab.oknotifier.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.modules.TestContestRepositoryModule;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.utils.DateUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, false, false);

//    @Inject
//    OpenKattisService openKattisService;
    @Inject
    ContestRepository contestRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .contestRepositoryModule(new TestContestRepositoryModule(app))
                .build();

        app.appComponent = testAppComponent;
        testAppComponent.inject(this);
    }

    @Test
    public void default_noContests_displaysEmptyView() {
        // given
        List<Contest> contests = new ArrayList<>();
        when(contestRepository.getAll()).thenReturn(contests);

        // when
        testRule.launchActivity(null);

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()));
    }

    @Test
    public void default_contests_displaysOngoingContests() {
        // given
        List<Contest> contests = createContests(10);
        when(contestRepository.getAll()).thenReturn(contests);

        // when
        testRule.launchActivity(null);

        // then
        onView(withId(R.id.contests_list_rv)).perform(RecyclerViewActions.scrollToPosition(contests.size() - 1));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    @Test
    public void toggleOrientation_displaysOngoingContests() {
        // given
        List<Contest> contests = createContests(10);
        when(contestRepository.getAll()).thenReturn(contests);

        testRule.launchActivity(null);

        // when
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // then
        onView(withId(R.id.contests_list_rv)).perform(RecyclerViewActions.scrollToPosition(contests.size() - 1));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    @Test
    public void toggleOrientationTwice_displaysOngoingContests() {
        // given
        List<Contest> contests = createContests(10);
        when(contestRepository.getAll()).thenReturn(contests);

        testRule.launchActivity(null);

        // when
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // then
        onView(withId(R.id.contests_list_rv)).perform(RecyclerViewActions.scrollToPosition(contests.size() - 1));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    private Contest createContest(int id) {
        String idString = String.format("id %s", id);
        String name = String.format("id %s", id);
        Date startDate = DateUtils.getDate(2000 + id, id, id, id, id, 0);
        Date endDate = DateUtils.getDate(2001 + id, id + 1, id + 1, id + 1, id + 1, 0);

        return new Contest(idString, name, startDate, endDate, id, id);
    }
    
    private List<Contest> createContests(int count) {
        List<Contest> contests = new ArrayList<>();

        for (int i = 1; i < count + 1; i++) {
            contests.add(createContest(i));
        }

        return contests;
    }
}
