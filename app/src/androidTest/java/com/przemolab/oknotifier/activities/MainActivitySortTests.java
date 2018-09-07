package com.przemolab.oknotifier.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.modules.NotifierRepositoryModule;
import com.przemolab.oknotifier.utils.DataHelper;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.przemolab.oknotifier.matchers.Matchers.isNotSubscribed;
import static com.przemolab.oknotifier.matchers.Matchers.isSubscribed;
import static com.przemolab.oknotifier.matchers.Matchers.withRecyclerView;

public class MainActivitySortTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setUp() {
        DataHelper.deleteTablesData(context);

        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
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
    public void sort_subscribedFirst_sortsContestsBySubscriptionFlag() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        String startDate = "2010-05-12 17:30:45";
        String endDate = "2010-05-12 17:30:45";
        DataHelper.insertContest(contentResolver, uri, "subscribed", "1", startDate, endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "not subscribed", "2", startDate, endDate, 0, 0, false);
        DataHelper.insertContest(contentResolver, uri, "subscribed", "3", startDate, endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "not subscribed", "4", startDate, endDate, 0, 0, false);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sort_menu_item)).perform(click());
        onView(withText("Subscribed first")).perform(click());

        // then
        Resources resources = context.getResources();
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestItem_fl))
                .check(matches(isSubscribed(resources)));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestItem_fl))
                .check(matches(isSubscribed(resources)));

        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestItem_fl))
                .check(matches(isNotSubscribed(resources)));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestItem_fl))
                .check(matches(isNotSubscribed(resources)));
    }

    @Test
    public void sort_byName_sortsContestsByName() {
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        String startDate = "2010-05-12 17:30:45";
        String endDate = "2010-05-12 17:30:45";
        DataHelper.insertContest(contentResolver, uri, "Bravo", "1", startDate, endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "Alpha", "2", startDate, endDate, 0, 0, false);
        DataHelper.insertContest(contentResolver, uri, "Zulu", "3", startDate, endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "Gamma", "4", startDate, endDate, 0, 0, false);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sort_menu_item)).perform(click());
        onView(withText("By name")).perform(click());

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("Alpha")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("Bravo")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("Gamma")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("Zulu")));
    }

    @Test
    public void sort_byStartDate_sortsContestsByStartDate() {
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        String endDate = "2010-05-12 17:30:45";
        DataHelper.insertContest(contentResolver, uri, "June", "1", "2010-06-12 17:30:45", endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "August", "2", "2010-08-12 17:30:45", endDate, 0, 0, false);
        DataHelper.insertContest(contentResolver, uri, "March", "3", "2010-03-12 17:30:45", endDate, 0, 0, true);
        DataHelper.insertContest(contentResolver, uri, "January", "4", "2010-01-12 17:30:45", endDate, 0, 0, false);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sort_menu_item)).perform(click());
        onView(withText("By start date")).perform(click());

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("January")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("March")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("June")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("August")));
    }

    @Test
    public void sort_byNumberOfContestants_sortsContestsByNumberOfContestants() {
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        String startDate = "2010-05-12 17:30:45";
        String endDate = "2010-05-12 17:30:45";
        DataHelper.insertContest(contentResolver, uri, "2 contestants", "1", startDate, endDate, 2, 0, true);
        DataHelper.insertContest(contentResolver, uri, "5 contestants", "2", startDate, endDate, 5, 0, false);
        DataHelper.insertContest(contentResolver, uri, "10 contestants", "3", startDate, endDate, 10, 0, true);
        DataHelper.insertContest(contentResolver, uri, "8 contestants", "4", startDate, endDate, 8, 0, false);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sort_menu_item)).perform(click());
        onView(withText("By # of contestants")).perform(click());

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("10 contestants")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("8 contestants")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("5 contestants")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("2 contestants")));
    }

    @Test
    public void sort_byNumberOfProblems_sortsContestsByNumberOfProblems() {
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        DataHelper.setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        String startDate = "2010-05-12 17:30:45";
        String endDate = "2010-05-12 17:30:45";
        DataHelper.insertContest(contentResolver, uri, "1 problem", "1", startDate, endDate, 0, 1, true);
        DataHelper.insertContest(contentResolver, uri, "5 problems", "2", startDate, endDate, 0, 5, false);
        DataHelper.insertContest(contentResolver, uri, "2 problems", "3", startDate, endDate, 0, 2, true);
        DataHelper.insertContest(contentResolver, uri, "10 problems", "4", startDate, endDate, 0, 10, false);

        testRule.launchActivity(null);

        // when
        onView(withId(R.id.sort_menu_item)).perform(click());
        onView(withText("By # of problems")).perform(click());

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("10 problems")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("5 problems")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("2 problems")));
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("1 problem")));
    }
}
