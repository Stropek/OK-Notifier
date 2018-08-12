package com.przemolab.oknotifier.matchers;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.przemolab.oknotifier.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {

    public static Matcher<View> isSubscribed(Resources resources) {
        return hasBackgroundColor(resources.getColor(R.color.lightGreen));
    }

    public static Matcher<View> isNotSubscribed(Resources resources) {
        return hasBackgroundColor(resources.getColor(R.color.lightGrey));
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private static Matcher<View> hasBackgroundColor(final int color) {

        Checks.checkNotNull(color);

        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public boolean matchesSafely(View view) {
                return color == ((ColorDrawable) view.getBackground()).getColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has background with color: " + color);
            }
        };
    }
}
