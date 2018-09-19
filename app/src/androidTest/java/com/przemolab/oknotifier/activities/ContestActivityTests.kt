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
import com.przemolab.oknotifier.models.Contestant
import com.przemolab.oknotifier.utils.DataHelper

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import java.util.ArrayList

import javax.inject.Inject

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.modules.*
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ContestActivityTests {

    private val context = InstrumentationRegistry.getTargetContext()

    @Rule
    @JvmField
    var testRule = ActivityTestRule(ContestActivity::class.java, false, false)

    @Inject
    @JvmField
    internal var openKattisService: IOpenKattisService? = null
    @Inject
    @JvmField
    internal var notifierRepository: INotifierRepository? = null

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
    fun default_noContestants_displaysEmptyView() {
        // given
        val contestants = ArrayList<Contestant>()
        `when`(notifierRepository!!.getAllContestants("contestId")).thenReturn(contestants)

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "contestId")

        // when
        testRule.launchActivity(startIntent)

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()))
    }

    @Test
    fun default_contestStandings_displaysContestStandings() {
        // given
        val contestants = DataHelper.createContestants(5, "abc")
        `when`(notifierRepository!!.getAllContestants("abc")).thenReturn(contestants)

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc")

        // when
        testRule.launchActivity(startIntent)

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(contestants.size - 1))
        onView(withText("name 4")).check(matches(isDisplayed()))
    }

    @Test
    fun syncClicked_contestantsLoaded() {
        // given
        val contestants = DataHelper.createContestants(5, "abc")
        `when`(openKattisService!!.getContestStandings("abc"))
                .thenAnswer(object : Answer<List<Contestant>> {
                    private var count = 0

                    override fun answer(invocation: InvocationOnMock): List<Contestant> {
                        count++
                        return if (count == 1) {
                            ArrayList()
                        } else contestants
                    }
                })

        val startIntent = Intent()
        startIntent.putExtra(Constants.BundleKeys.ContestId, "abc")

        testRule.launchActivity(startIntent)

        // when
        onView(withId(R.id.sync_menu_item)).perform(click())

        // then
        onView(withId(R.id.contestantsList_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(contestants.size - 1))
        onView(withText("name 5")).check(matches(isDisplayed()))
    }
}
