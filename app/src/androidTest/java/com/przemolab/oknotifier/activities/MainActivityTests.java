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
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.TestNotifierRepositoryModule;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.utils.DataHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.przemolab.oknotifier.matchers.Matchers.isNotSubscribed;
import static com.przemolab.oknotifier.matchers.Matchers.isSubscribed;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Inject
    OpenKattisService openKattisService;
    @Inject
    NotifierRepository notifierRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(new TestOpenKattisServiceModule())
                .notifierRepositoryModule(new TestNotifierRepositoryModule(app))
                .build();

        app.appComponent = testAppComponent;
        testAppComponent.inject(this);
    }

    @Test
    public void default_noContests_displaysEmptyView() {
        // given
        List<Contest> contests = new ArrayList<>();
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        // when
        testRule.launchActivity(null);

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()));
    }

    @Test
    public void default_contests_displaysOngoingContests() {
        // given
        List<Contest> contests = new ArrayList<>();
        contests.add(DataHelper.Companion.createContest(1));
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        // when
        testRule.launchActivity(null);

        // then
        onView(withText("id 1")).check(matches(isDisplayed()));
        onView(withId(R.id.contestItem_fl)).check(matches(isNotSubscribed(context.getResources())));
        }

    @Test
    public void default_contestsSubscribed_displaysContestWithAlternateColor() {
        // given
        List<Contest> contests = new ArrayList<>();
        contests.add(DataHelper.Companion.createContest(1, true));
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        // when
        testRule.launchActivity(null);

        // then
        onView(withText("id 1")).check(matches(isDisplayed()));
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context.getResources())));
    }

    @Test
    public void toggleSubscription_contestNotSubscribed_displaysContestWithAlternateColor() {
        // given
        List<Contest> contests = new ArrayList<>();
        contests.add(DataHelper.Companion.createContest(1));
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.subscribe_ib)).perform(click());

        // then
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context.getResources())));
    }

    @Test
    public void toggleOrientation_displaysOngoingContests() {
        // given
        List<Contest> contests = DataHelper.Companion.createContests(10);
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        testRule.launchActivity(null);

        // when
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition(contests.size() - 1));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    @Test
    public void toggleOrientationTwice_displaysOngoingContests() {
        // given
        List<Contest> contests = DataHelper.Companion.createContests(10);
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);

        testRule.launchActivity(null);

        // when
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        testRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition(contests.size() - 1));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    @Test
    public void contestClicked_contestantsInRepository_activityWithContestStandingsOpens() {
        // given
        List<Contest> contests = DataHelper.Companion.createContests(1);
        List<Contestant> contestants = DataHelper.Companion.createContestants(3, "id 1");
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);
        when(notifierRepository.getAllContestants("id 1")).thenReturn(contestants);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.contestItem_fl)).perform(click());

        // then
        onView(withText("name 1")).check(matches(isDisplayed()));
        onView(withText("name 2")).check(matches(isDisplayed()));
        onView(withText("name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void contestClicked_contestantsNotInRepository_activityLoadsContestStandingsFromOpenKattis() {
        // given
        List<Contest> contests = DataHelper.Companion.createContests(1);
        when(notifierRepository.getAll(SortOrder.SubscribedFirst)).thenReturn(contests);
        when(notifierRepository.getAllContestants("id 1")).thenReturn(new ArrayList<Contestant>());

        List<Contestant> contestants = DataHelper.Companion.createContestants(3, "id 1");
        when(openKattisService.getContestStandings("id 1")).thenReturn(contestants);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.contestItem_fl)).perform(click());

        // then
        onView(withText("name 1")).check(matches(isDisplayed()));
        onView(withText("name 2")).check(matches(isDisplayed()));
        onView(withText("name 3")).check(matches(isDisplayed()));
    }
}