package com.przemolab.oknotifier;

import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.dx.cf.code.Frame;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Matchers {

    public static Matcher<View> hasBackgroundColor(final int color) {

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
