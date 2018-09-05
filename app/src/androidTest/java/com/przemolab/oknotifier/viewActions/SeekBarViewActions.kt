package com.przemolab.oknotifier.viewActions

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers
import android.view.View
import android.widget.SeekBar

import org.hamcrest.Matcher

object SeekBarViewActions {

    fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController, view: View) {
                (view as SeekBar).progress = progress
            }

            override fun getDescription(): String {
                return "Set a progress"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(SeekBar::class.java)
            }
        }
    }
}
