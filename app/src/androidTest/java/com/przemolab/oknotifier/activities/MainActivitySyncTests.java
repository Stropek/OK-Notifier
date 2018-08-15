package com.przemolab.oknotifier.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.ContestRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.After;
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
import static com.przemolab.oknotifier.matchers.Matchers.isSubscribed;
import static com.przemolab.oknotifier.utils.DataHelper.createContest;
import static com.przemolab.oknotifier.utils.DataHelper.createContests;
import static com.przemolab.oknotifier.utils.DataHelper.deleteTablesData;
import static com.przemolab.oknotifier.utils.DataHelper.insertContest;
import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivitySyncTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Inject
    OpenKattisService openKattisService;

    @Before
    public void setUp() {
        deleteTablesData(context);

        MockitoAnnotations.initMocks(this);
        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(new TestOpenKattisServiceModule())
                .contestRepositoryModule(new ContestRepositoryModule(app))
                .build();

        app.appComponent = testAppComponent;
        testAppComponent.inject(this);
    }

    @After
    public void cleanUp() {
        deleteTablesData(context);
    }

    @Test
    public void syncClicked_noContestsInDatabase_addContestToDatabase() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        List<Contest> ongoingContests = createContests(5);
        when(openKattisService.getOngoingContests()).thenReturn(ongoingContests);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sync_ib)).perform(click());

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition(ongoingContests.size() - 1));
        onView(withText("id 5")).check(matches(isDisplayed()));
    }

    @Test
    public void syncClicked_oldContestsInDatabase_removesOldContestsFromDatabase() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        List<Contest> existingContests = createContests(10);
        for (Contest contest : existingContests) {
            insertContest(contentResolver, uri, contest);
        }

        when(openKattisService.getOngoingContests()).thenReturn(existingContests.subList(5, 10));

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sync_menu_item)).perform(click());

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition(4));
        onView(withText("id 10")).check(matches(isDisplayed()));
    }

    @Test
    public void syncClicked_currentContestsInDatabase_updatesExistingContests() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        List<Contest> existingContests = createContests(5);
        for (Contest contest : existingContests) {
            insertContest(contentResolver, uri, contest);
        }

        for (Contest modifiedContest : existingContests) {
            modifiedContest.setName(modifiedContest.getName() + " modified");
        }

        when(openKattisService.getOngoingContests()).thenReturn(existingContests);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sync_menu_item)).perform(click());

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition(4));
        onView(withText("id 5 modified")).check(matches(isDisplayed()));
    }

    @Test
    public void syncClicked_contestSubscribed_contestStaysSubscribed() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        TestContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        Contest subscribedContest = createContest(1);
        subscribedContest.setSubscribed(true);
        insertContest(contentResolver, uri, subscribedContest);

        subscribedContest.setName(subscribedContest.getName() + " modified");
        List<Contest> ongoingContests = new ArrayList<>();
        ongoingContests.add(subscribedContest);

        when(openKattisService.getOngoingContests()).thenReturn(ongoingContests);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sync_menu_item)).perform(click());

        // then
        onView(withText("id 1 modified")).check(matches(isDisplayed()));
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context.getResources())));
    }
}
