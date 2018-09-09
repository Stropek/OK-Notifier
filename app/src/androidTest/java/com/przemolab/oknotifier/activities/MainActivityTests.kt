package com.przemolab.oknotifier.activities

import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.DaggerTestAppComponent
import com.przemolab.oknotifier.NotifierApp
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.models.Contest
import com.przemolab.oknotifier.utils.DataHelper

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

import java.util.ArrayList

import javax.inject.Inject

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.matchers.Matchers.isNotSubscribed
import com.przemolab.oknotifier.matchers.Matchers.isSubscribed
import com.przemolab.oknotifier.modules.*
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MainActivityTests {

    private val context = InstrumentationRegistry.getTargetContext()

    @Rule
    @JvmField
    var testRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Inject
    @JvmField
    var openKattisService: IOpenKattisService? = null
    @Inject
    @JvmField
    var notifierRepository: INotifierRepository? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val app = context.applicationContext as NotifierApp

        val testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(TestOpenKattisServiceModule())
                .notifierRepositoryModule(TestNotifierRepositoryModule(app))
                .build()

        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    @Test
    fun default_noContests_displaysEmptyView() {
        // given
        val contests = ArrayList<Contest>()
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        // when
        testRule.launchActivity(null)

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()))
    }

    @Test
    fun default_contests_displaysOngoingContests() {
        // given
        val contests = ArrayList<Contest>()
        contests.add(DataHelper.createContest(1))
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        // when
        testRule.launchActivity(null)

        // then
        onView(withText("id 1")).check(matches(isDisplayed()))
        onView(withId(R.id.contestItem_fl)).check(matches(isNotSubscribed(context)))
    }

    @Test
    fun default_contestsSubscribed_displaysContestWithAlternateColor() {
        // given
        val contests = ArrayList<Contest>()
        contests.add(DataHelper.createContest(1, true))
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        // when
        testRule.launchActivity(null)

        // then
        onView(withText("id 1")).check(matches(isDisplayed()))
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context)))
    }

    @Test
    fun toggleSubscription_contestNotSubscribed_displaysContestWithAlternateColor() {
        // given
        val contests = ArrayList<Contest>()
        contests.add(DataHelper.createContest(1))
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.subscribe_ib)).perform(click())

        // then
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context)))
    }

    @Test
    fun toggleOrientation_displaysOngoingContests() {
        // given
        val contests = DataHelper.createContests(10)
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        testRule.launchActivity(null)

        // when
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(contests.size - 1))
        onView(withText("id 10")).check(matches(isDisplayed()))
    }

    @Test
    fun toggleOrientationTwice_displaysOngoingContests() {
        // given
        val contests = DataHelper.createContests(10)
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)

        testRule.launchActivity(null)

        // when
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        testRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(contests.size - 1))
        onView(withText("id 10")).check(matches(isDisplayed()))
    }

    @Test
    fun contestClicked_contestantsInRepository_activityWithContestStandingsOpens() {
        // given
        val contests = DataHelper.createContests(1)
        val contestants = DataHelper.createContestants(3, "id 1")
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)
        `when`(notifierRepository!!.getAllContestants("id 1")).thenReturn(contestants)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.contestItem_fl)).perform(click())

        // then
        onView(withText("name 1")).check(matches(isDisplayed()))
        onView(withText("name 2")).check(matches(isDisplayed()))
        onView(withText("name 3")).check(matches(isDisplayed()))
    }

    @Test
    fun contestClicked_contestantsNotInRepository_activityLoadsContestStandingsFromOpenKattis() {
        // given
        val contests = DataHelper.createContests(1)
        `when`(notifierRepository!!.getAll(SortOrder.SubscribedFirst)).thenReturn(contests)
        `when`(notifierRepository!!.getAllContestants("id 1")).thenReturn(ArrayList())

        val contestants = DataHelper.createContestants(3, "id 1")
        `when`(openKattisService!!.getContestStandings("id 1")).thenReturn(contestants)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.contestItem_fl)).perform(click())

        // then
        onView(withText("name 1")).check(matches(isDisplayed()))
        onView(withText("name 2")).check(matches(isDisplayed()))
        onView(withText("name 3")).check(matches(isDisplayed()))
    }
}