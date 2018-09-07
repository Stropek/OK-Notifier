package com.przemolab.oknotifier.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.utils.DataHelper;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

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
public class ContestActivitySyncTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<ContestActivity> testRule = new ActivityTestRule<>(ContestActivity.class, false, false);

    @Inject
    OpenKattisService openKattisService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(new TestOpenKattisServiceModule())
                .notifierRepositoryModule(new NotifierRepositoryModule(app))
                .build();

        app.appComponent = testAppComponent;
        testAppComponent.inject(this);
    }

    @After
    public void cleanUp() {
        DataHelper.deleteTablesData(context);
    }

    @Test
    public void syncClicked_noContestantsInDatabase_addContestantsToDatabase() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestantEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        List<Contestant> contestants = DataHelper.createContestants(5, "abc");
        when(openKattisService.getContestStandings("abc")).thenReturn(contestants);

        Intent startIntent = new Intent();
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc");

        // when
        testRule.launchActivity(startIntent);

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition(contestants.size() - 1));
        onView(withText("name 5")).check(matches(isDisplayed()));
    }

    @Test
    public void syncClicked_currentContestsInDatabase_updatesExistingContests() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestantEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        List<Contestant> existingContestants = DataHelper.createContestants(5, "abc");
        for (Contestant contestant : existingContestants) {
            DataHelper.insertContestant(contentResolver, uri, contestant.getContestId(), String.format("name %s", contestant.getId()));
        }

        Contestant modifiedContestant = existingContestants.get(existingContestants.size() - 1);
        modifiedContestant.setProblemsSolved(modifiedContestant.getProblemsSolved() + 100);
        modifiedContestant.setProblemsSubmitted(modifiedContestant.getProblemsSubmitted() + 100);
        modifiedContestant.setProblemsFailed(modifiedContestant.getProblemsFailed() + 100);
        modifiedContestant.setProblemsNotTried(modifiedContestant.getProblemsNotTried() + 100);
        modifiedContestant.setTime(modifiedContestant.getTime() + 100);

        when(openKattisService.getContestStandings("abc")).thenReturn(existingContestants);

        Intent startIntent = new Intent();
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc");

        testRule.launchActivity(startIntent);

        // when
        onView(withId(R.id.sync_menu_item)).perform(click());

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition(existingContestants.size() - 1));
        onView(withText("101")).check(matches(isDisplayed()));
        onView(withText("102")).check(matches(isDisplayed()));
        onView(withText("103")).check(matches(isDisplayed()));
        onView(withText("104")).check(matches(isDisplayed()));
        onView(withText("105")).check(matches(isDisplayed()));
    }
}
