package com.przemolab.oknotifier.matchers

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.test.espresso.intent.Checks
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v4.content.ContextCompat
import android.view.View

import com.przemolab.oknotifier.R

import org.hamcrest.Description
import org.hamcrest.Matcher

object Matchers {

    @JvmStatic
    fun isSubscribed(context: Context): Matcher<View> {
        return hasBackgroundColor(ContextCompat.getColor(context, R.color.lightGreen))
    }

    @JvmStatic
    fun isNotSubscribed(context: Context): Matcher<View> {
        return hasBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
    }

    @JvmStatic
    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    private fun hasBackgroundColor(color: Int): Matcher<View> {

        Checks.checkNotNull(color)

        return object : BoundedMatcher<View, View>(View::class.java) {

            public override fun matchesSafely(view: View): Boolean {
                return color == (view.background as ColorDrawable).color
            }

            override fun describeTo(description: Description) {
                description.appendText("has background with color: $color")
            }
        }
    }
}
