package com.przemolab.oknotifier.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;

import com.przemolab.oknotifier.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.przemolab.oknotifier.viewActions.SeekBarViewActions.setProgress;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<SettingsActivity> testRule = new ActivityTestRule<>(SettingsActivity.class, false, false);

    @Before
    public void setUp() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().apply();
    }

    @Test
    public void default_displaysCustomSettingsWithDefaultValues() {
        // when
        testRule.launchActivity(null);

        // then
        onView(withId(R.id.switchesLabel_tv)).check(matches(isDisplayed()));
        onView(withId(R.id.switches_ll)).check(matches(isDisplayed()));
        onView(withId(R.id.approved_tb)).check(matches(isDisplayed()));
        onView(withId(R.id.submitted_tb)).check(matches(isDisplayed()));
        onView(withId(R.id.rejected_tb)).check(matches(isDisplayed()));
        onView(withId(R.id.title_tv)).check(matches(isDisplayed()));
        onView(withId(R.id.value_sb)).check(matches(isDisplayed()));
        onView(withId(R.id.value_tv)).check(matches(isDisplayed()));
    }

    @Test
    public void changeContestSwitchesState_persistsSwitchesState() {
        // given
        testRule.launchActivity(null);

        // when
        onView(withId(R.id.approved_tb)).perform(click());
        onView(withId(R.id.submitted_tb)).perform(click());
        onView(withId(R.id.rejected_tb)).perform(click());

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Preferences")).perform(click());

        // then
        onView(withId(R.id.approved_tb)).check(matches(not(isChecked())));
        onView(withId(R.id.submitted_tb)).check(matches(isChecked()));
        onView(withId(R.id.rejected_tb)).check(matches(isChecked()));
    }

    @Test
    public void changeChangeCheckFrequency_persistsFrequency() {
        // given
        testRule.launchActivity(null);

        // when
        onView(withId(R.id.value_sb)).perform(setProgress(50));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Preferences")).perform(click());

        // then
        onView(withId(R.id.value_tv)).check(matches(withText("50m")));
    }
}
