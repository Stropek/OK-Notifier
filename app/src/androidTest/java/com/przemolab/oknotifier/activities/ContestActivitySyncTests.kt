package com.przemolab.oknotifier.activities

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.przemolab.oknotifier.Constants
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

import javax.inject.Inject

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import com.przemolab.oknotifier.data.AppDatabase
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import org.junit.Assert.assertEquals
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ContestActivitySyncTests {

    private val context = InstrumentationRegistry.getTargetContext()
    private val db = AppDatabase.getInstance(context)!!

    @Rule
    @JvmField
    var testRule = ActivityTestRule(ContestActivity::class.java, false, false)

    @Inject
    @JvmField
    internal var openKattisService: IOpenKattisService? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val app = context.applicationContext as NotifierApp

        val testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(TestOpenKattisServiceModule())
                .notifierRepositoryModule(NotifierRepositoryModule(app))
                .build()

        app.appComponent = testAppComponent
        testAppComponent.inject(this)

        DataHelper.deleteTablesData(db)
    }

    @After
    fun cleanUp() {
        DataHelper.deleteTablesData(db)
    }

    @Test
    fun syncClicked_noContestantsInDatabase_addContestantsToDatabase() {
        // given
        val contestants = DataHelper.createContestantEntries(5, "abc")
        `when`(openKattisService!!.getContestStandings("abc")).thenReturn(contestants)

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc")

        // when
        testRule.launchActivity(startIntent)

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(contestants.size - 1))
        onView(withText("name 5")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_currentContestantsInDatabase_updatesExistingContestants() {
        // given
        val existingContestants = DataHelper.createContestantEntries(5, "abc")
        for (contestant in existingContestants) {
            db.contestantDao().insert(contestant)
        }

        val modifiedContestant = existingContestants[existingContestants.size - 1]
        modifiedContestant.problemsSolved = modifiedContestant.problemsSolved + 100
        modifiedContestant.problemsSubmitted = modifiedContestant.problemsSubmitted + 100
        modifiedContestant.problemsFailed = modifiedContestant.problemsFailed + 100
        modifiedContestant.problemsNotTried = modifiedContestant.problemsNotTried + 100
        modifiedContestant.time = modifiedContestant.time + 100

        `when`(openKattisService!!.getContestStandings("abc")).thenReturn(existingContestants)

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc")

        testRule.launchActivity(startIntent)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(existingContestants.size - 1))
        onView(withText("101")).check(matches(isDisplayed()))
        onView(withText("102")).check(matches(isDisplayed()))
        onView(withText("103")).check(matches(isDisplayed()))
        onView(withText("104")).check(matches(isDisplayed()))
        onView(withText("105")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_newContestants_updatesNumberOfContestantsForContest() {
        // given
        val db = AppDatabase.getInstance(context)!!
        db.contestDao().insert(DataHelper.createContestEntry(1, contestId = "abc"))

        val contestants = DataHelper.createContestantEntries(5, "abc")
        `when`(openKattisService!!.getContestStandings("abc")).thenReturn(contestants)

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc")

        testRule.launchActivity(startIntent)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        val updatedContest = db.contestDao().getAll().first { it -> it.id == 1 }
        assertEquals(5, updatedContest.numberOfContestants)
        assertEquals("abc", updatedContest.contestId)
    }
}
