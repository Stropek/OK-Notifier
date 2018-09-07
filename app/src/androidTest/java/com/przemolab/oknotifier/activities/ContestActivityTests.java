package com.przemolab.oknotifier.activities;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.modules.TestNotifierRepositoryModule;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.utils.DataHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ContestActivityTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<ContestActivity> testRule = new ActivityTestRule<>(ContestActivity.class, false, false);

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
    public void default_noContestants_displaysEmptyView() {
        // given
        List<Contestant> contestants = new ArrayList<>();
        when(notifierRepository.getAllContestants("contestId")).thenReturn(contestants);

        Intent startIntent = new Intent();
        startIntent.putExtra(Constants.BundleKeys.ContestId, "contestId");

        // when
        testRule.launchActivity(startIntent);

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()));
    }

    @Test
    public void default_contestStandings_displaysContestStandings() {
        // given
        List<Contestant> contestants = DataHelper.Companion.createContestants(5, "abc");
        when(notifierRepository.getAllContestants("abc")).thenReturn(contestants);

        Intent startIntent = new Intent();
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc");

        // when
        testRule.launchActivity(startIntent);

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition(contestants.size() - 1));
        onView(withText("name 4")).check(matches(isDisplayed()));
    }

    @Test
    public void syncClicked_contestantsLoaded() {
        // given
        final List<Contestant> contestants = DataHelper.Companion.createContestants(5, "abc");
        when(openKattisService.getContestStandings("abc"))
                .thenAnswer(new Answer<List<Contestant>>() {
                    private int count = 0;

                    @Override
                    public List<Contestant> answer(InvocationOnMock invocation) {
                        count++;
                        if (count == 1) {
                            return new ArrayList<>();
                        }
                        return contestants;
                    }
                });

        Intent startIntent = new Intent();
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc");

        testRule.launchActivity(startIntent);

        // when
        onView(withId(R.id.sync_menu_item)).perform(click());

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition(contestants.size() - 1));
        onView(withText("name 5")).check(matches(isDisplayed()));
    }
}
