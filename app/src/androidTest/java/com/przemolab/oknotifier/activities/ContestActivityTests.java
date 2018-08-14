package com.przemolab.oknotifier.activities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.DaggerTestAppComponent;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.TestAppComponent;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.modules.TestContestRepositoryModule;
import com.przemolab.oknotifier.modules.TestOpenKattisServiceModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ContestActivityTests {

    private final Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<ContestActivity> testRule = new ActivityTestRule<>(ContestActivity.class, false, false);

    @Inject
    OpenKattisService openKattisService;
    @Inject
    ContestRepository contestRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        NotifierApp app = (NotifierApp) context.getApplicationContext();

        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .openKattisServiceModule(new TestOpenKattisServiceModule())
                .contestRepositoryModule(new TestContestRepositoryModule(app))
                .build();

        app.appComponent = testAppComponent;
        testAppComponent.inject(this);
    }

    @Test
    public void default_noContestants_displaysEmptyView() {
        // given
        List<Contestant> contestants = new ArrayList<>();
        when(contestRepository.getAllContestants("contestId")).thenReturn(contestants);

        // when
        testRule.launchActivity(null);

        // then
        onView(withId(R.id.empty_cl)).check(matches(isDisplayed()));
    }
}
