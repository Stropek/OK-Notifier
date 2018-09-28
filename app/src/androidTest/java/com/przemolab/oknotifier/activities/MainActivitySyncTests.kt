package com.przemolab.oknotifier.activities

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.DaggerTestAppComponent
import com.przemolab.oknotifier.NotifierApp
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.modules.NotifierRepositoryModule
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule
import com.przemolab.oknotifier.utils.DataHelper

import org.junit.After
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
import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.matchers.Matchers.isSubscribed
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MainActivitySyncTests {

    private val context = InstrumentationRegistry.getTargetContext()
    private val db = AppDatabase.getInstance(context)!!

    @Rule
    @JvmField
    var testRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Inject
    @JvmField
    internal var openKattisService: IOpenKattisService? = null

    @Before
    fun setUp() {
        DataHelper.deleteTablesData(db)

        MockitoAnnotations.initMocks(this)
        val app = context.applicationContext as NotifierApp

        val testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(TestOpenKattisServiceModule())
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
    fun syncClicked_noContestsInDatabase_addContestToDatabase() {
        // given
        val ongoingContests = DataHelper.createContestEntries(5)
        `when`(openKattisService!!.ongoingContests).thenReturn(ongoingContests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sync_ib)).perform(click())

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(ongoingContests.size - 1))
        onView(withText("id 5")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_oldContestsInDatabase_removesOldContestsFromDatabase() {
        // given
        val existingContests = DataHelper.createContestEntries(8)
        db.contestDao().insertMany(existingContests)

        `when`(openKattisService!!.ongoingContests).thenReturn(existingContests.subList(4, 8))

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(3))
        onView(withText("id 8")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_currentContestsInDatabase_updatesExistingContests() {
        // given
        val existingContests = DataHelper.createContestEntries(5)
        db.contestDao().insertMany(existingContests)

        for (modifiedContest in existingContests) {
            modifiedContest.name = modifiedContest.name + " modified"
        }

        `when`(openKattisService!!.ongoingContests).thenReturn(existingContests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        onView(withId(R.id.contestsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
        onView(withText("id 5 modified")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_contestSubscribed_contestStaysSubscribed() {
        // given
        val subscribedContest = DataHelper.createContestEntry(1)
        subscribedContest.subscribed = true
        db.contestDao().insert(subscribedContest)

        subscribedContest.name = subscribedContest.name + " modified"
        val ongoingContests = ArrayList<ContestEntry>()
        ongoingContests.add(subscribedContest)

        `when`(openKattisService!!.ongoingContests).thenReturn(ongoingContests)

        testRule.launchActivity(null)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        onView(withText("id 1 modified")).check(matches(isDisplayed()))
        onView(withId(R.id.contestItem_fl)).check(matches(isSubscribed(context)))
    }
}