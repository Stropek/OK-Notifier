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
import com.przemolab.oknotifier.data.ContestContract;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.ContestRepositoryModule;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;
import com.przemolab.oknotifier.utils.DateUtils;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.After;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

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
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

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
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

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
