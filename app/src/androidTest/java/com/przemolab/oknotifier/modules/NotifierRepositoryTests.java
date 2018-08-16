package com.przemolab.oknotifier.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.przemolab.oknotifier.utils.DataHelper.deleteTablesData;
import static com.przemolab.oknotifier.utils.DataHelper.insertContest;
import static com.przemolab.oknotifier.utils.DataHelper.insertContestant;
import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NotifierRepositoryTests {

    private static final Context context = InstrumentationRegistry.getTargetContext();

    @BeforeClass
    public static void setUp() {
        deleteTablesData(context);
    }

    @AfterClass
    public static void cleanUp() {
        deleteTablesData(context);
    }

    @Test
    public void getAll_returnsAllContest() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            insertContest(contentResolver, uri, "contest " + i, "abc" + i,
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i*3, i*2);
        }

        NotifierRepository notifierRepository = new NotifierRepository(context);

        // when
        List<Contest> result = notifierRepository.getAll(SortOrder.SubscribedFirst);

        // then
        assertEquals(10, result.size());
    }

    @Test
    public void getAllContestants_contestIdPassed_returnsAllContestantsWithGivenContestId() {
        // given
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = TestContentObserver.getTestContentObserver();
        Uri uri = NotifierContract.ContestantEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                insertContestant(contentResolver, uri, "abc");
            } else {
                insertContestant(contentResolver, uri, "zxy");
            }
        }

        NotifierRepository notifierRepository = new NotifierRepository(context);

        // when
        List<Contestant> result = notifierRepository.getAllContestants("abc");

        // then
        assertEquals(5, result.size());
        for (Contestant contestant : result) {
            assertEquals("abc", contestant.getContestId());
        }
    }
}
