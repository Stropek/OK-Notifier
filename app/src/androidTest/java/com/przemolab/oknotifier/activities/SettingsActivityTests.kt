package com.przemolab.oknotifier.activities

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.preference.PreferenceManager

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.viewActions.SeekBarViewActions

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isChecked
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers.not

@RunWith(AndroidJUnit4::class)
class SettingsActivityTests {

    private val context = InstrumentationRegistry.getTargetContext()

    @Rule
    @JvmField
    var testRule = ActivityTestRule(SettingsActivity::class.java, false, false)

    @Before
    fun setUp() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().putBoolean(Constants.SharedPreferences.NotificationsSwitch, true).apply()
    }

    @Test
    fun default_displaysCustomSettingsWithDefaultValues() {
        // when
        testRule.launchActivity(null)

        // then
        onView(withId(R.id.switchesLabel_tv)).check(matches(isDisplayed()))
        onView(withId(R.id.switches_ll)).check(matches(isDisplayed()))
        onView(withId(R.id.approved_tb)).check(matches(isDisplayed()))
        onView(withId(R.id.submitted_tb)).check(matches(isDisplayed()))
        onView(withId(R.id.rejected_tb)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(9))
        onView(withId(R.id.title_tv)).check(matches(isDisplayed()))
        onView(withId(R.id.value_sb)).check(matches(isDisplayed()))
        onView(withId(R.id.value_tv)).check(matches(isDisplayed()))
    }

    @Test
    fun changeContestSwitchesState_persistsSwitchesState() {
        // given
        testRule.launchActivity(null)

        // when
        onView(withId(R.id.approved_tb)).perform(click())
        onView(withId(R.id.submitted_tb)).perform(click())
        onView(withId(R.id.rejected_tb)).perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.settings_menu_item)).perform(click())

        // then
        onView(withId(R.id.approved_tb)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.submitted_tb)).check(matches(isChecked()))
        onView(withId(R.id.rejected_tb)).check(matches(isChecked()))
    }

    @Test
    fun changeCheckFrequency_persistsFrequency() {
        // given
        testRule.launchActivity(null)

        // when
        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(9))
        onView(withId(R.id.value_sb)).perform(SeekBarViewActions.setProgress(50))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.settings_menu_item)).perform(click())

        // then
        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(9))
        onView(withId(R.id.value_tv)).check(matches(withText("50m")))
    }
}
