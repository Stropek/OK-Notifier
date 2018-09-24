package com.przemolab.oknotifier.activities

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule

import com.przemolab.oknotifier.DaggerTestAppComponent
import com.przemolab.oknotifier.NotifierApp
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.modules.NotifierRepositoryModule
import com.przemolab.oknotifier.utils.DataHelper

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.data.ContestEntry
import com.przemolab.oknotifier.matchers.Matchers.isNotSubscribed
import com.przemolab.oknotifier.matchers.Matchers.isSubscribed
import com.przemolab.oknotifier.matchers.Matchers.withRecyclerView
import com.przemolab.oknotifier.utils.DateUtils

class MainActivitySortTests {

    private val context = InstrumentationRegistry.getTargetContext()
    private val db = AppDatabase.getInstance(context)!!

    @Rule
    @JvmField
    var testRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(db)

        val app = context.applicationContext as NotifierApp

        val testAppComponent = DaggerTestAppComponent.builder()
                .notifierRepositoryModule(NotifierRepositoryModule(app))
                .build()

        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun sort_subscribedFirst_sortsContestsBySubscriptionFlag() {
        // given
        val startDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val endDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val contests = listOf(
                ContestEntry(contestId = "1", name = "subscribed", subscribed = true, startDate = startDate, endDate = endDate, numberOfProblems = 0, numberOfContestants = 0),
                ContestEntry(contestId = "2", name = "not subscribed", subscribed = false, startDate = startDate, endDate = endDate, numberOfProblems = 0, numberOfContestants = 0),
                ContestEntry(contestId = "3", name = "subscribed", subscribed = true, startDate = startDate, endDate = endDate, numberOfProblems = 0, numberOfContestants = 0),
                ContestEntry(contestId = "4", name = "not subscribed", subscribed = false, startDate = startDate, endDate = endDate, numberOfProblems = 0, numberOfContestants = 0)
        )
        db.contestDao().insertMany(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sort_menu_item)).perform(click())
        onView(withText("Subscribed first")).perform(click())

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestItem_fl))
                .check(matches(isSubscribed(context)))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestItem_fl))
                .check(matches(isSubscribed(context)))

        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestItem_fl))
                .check(matches(isNotSubscribed(context)))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestItem_fl))
                .check(matches(isNotSubscribed(context)))
    }

    @Test
    fun sort_byName_sortsContestsByName() {
        // given
        val startDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val endDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val contests = listOf(
                ContestEntry(contestId = "1", name = "Bravo", subscribed = true, startDate = startDate, endDate = endDate),
                ContestEntry(contestId = "2", name = "Alpha", subscribed = false, startDate = startDate, endDate = endDate),
                ContestEntry(contestId = "3", name = "Zulu", subscribed = true, startDate = startDate, endDate = endDate),
                ContestEntry(contestId = "4", name = "Gamma", subscribed = false, startDate = startDate, endDate = endDate)
        )
        db.contestDao().insertMany(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sort_menu_item)).perform(click())
        onView(withText("By name")).perform(click())

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("Alpha")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("Bravo")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("Gamma")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("Zulu")))
    }

    @Test
    fun sort_byStartDate_sortsContestsByStartDate() {
        // given
        val endDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val contests = listOf(
                ContestEntry(contestId = "1", name = "June", subscribed = true, startDate = DateUtils.getDate("2010-06-12 17:30:45", DateUtils.SQLiteDateTimeFormat), endDate = endDate),
                ContestEntry(contestId = "2", name = "August", subscribed = false, startDate = DateUtils.getDate("2010-08-12 17:30:45", DateUtils.SQLiteDateTimeFormat), endDate = endDate),
                ContestEntry(contestId = "3", name = "March", subscribed = true, startDate = DateUtils.getDate("2010-03-12 17:30:45", DateUtils.SQLiteDateTimeFormat), endDate = endDate),
                ContestEntry(contestId = "4", name = "January", subscribed = false, startDate = DateUtils.getDate("2010-01-12 17:30:45", DateUtils.SQLiteDateTimeFormat), endDate = endDate)
        )
        db.contestDao().insertMany(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sort_menu_item)).perform(click())
        onView(withText("By start date")).perform(click())

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("January")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("March")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("June")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("August")))
    }

    @Test
    fun sort_byNumberOfContestants_sortsContestsByNumberOfContestants() {
        val startDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val endDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val contests = listOf(
                ContestEntry(contestId = "1", name = "2 contestants", subscribed = true, startDate = startDate, endDate = endDate, numberOfContestants = 2),
                ContestEntry(contestId = "2", name = "5 contestants", subscribed = false, startDate = startDate, endDate = endDate, numberOfContestants = 5),
                ContestEntry(contestId = "3", name = "10 contestants", subscribed = true, startDate = startDate, endDate = endDate, numberOfContestants = 10),
                ContestEntry(contestId = "4", name = "8 contestants", subscribed = false, startDate = startDate, endDate = endDate, numberOfContestants = 8)
        )
        db.contestDao().insertMany(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sort_menu_item)).perform(click())
        onView(withText("By # of contestants")).perform(click())

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("10 contestants")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("8 contestants")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("5 contestants")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("2 contestants")))
    }

    @Test
    fun sort_byNumberOfProblems_sortsContestsByNumberOfProblems() {
        // given
        val startDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val endDate = DateUtils.getDate("2010-05-12 17:30:45", DateUtils.SQLiteDateTimeFormat)
        val contests = listOf(
                ContestEntry(contestId = "1", name = "1 problem", subscribed = true, startDate = startDate, endDate = endDate, numberOfProblems = 1),
                ContestEntry(contestId = "2", name = "5 problems", subscribed = false, startDate = startDate, endDate = endDate, numberOfProblems = 5),
                ContestEntry(contestId = "3", name = "2 problems", subscribed = true, startDate = startDate, endDate = endDate, numberOfProblems = 2),
                ContestEntry(contestId = "4", name = "10 problems", subscribed = false, startDate = startDate, endDate = endDate, numberOfProblems = 10)
        )
        db.contestDao().insertMany(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sort_menu_item)).perform(click())
        onView(withText("By # of problems")).perform(click())

        // then
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(0, R.id.contestName_tv))
                .check(matches(withText("10 problems")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(1, R.id.contestName_tv))
                .check(matches(withText("5 problems")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(2, R.id.contestName_tv))
                .check(matches(withText("2 problems")))
        onView(withRecyclerView(R.id.contestsList_rv).atPositionOnView(3, R.id.contestName_tv))
                .check(matches(withText("1 problem")))
    }
}
