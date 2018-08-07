package com.przemolab.oknotifier.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.przemolab.oknotifier.data.ContestContract;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.utils.TestContentObserver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.przemolab.oknotifier.utils.DataHelper.deleteTablesData;
import static com.przemolab.oknotifier.utils.DataHelper.insertContest;
import static com.przemolab.oknotifier.utils.DataHelper.setObservedUriOnContentResolver;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContestRepositoryTests {

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
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

        setObservedUriOnContentResolver(contentResolver, uri, contentObserver);

        for (int i = 0; i < 10; i++) {
            insertContest(contentResolver, uri, "contest " + i, "abc" + i,
                    "2010-10-16 16:00:00", "2010-10-26 18:00:00", i*3, i*2);
        }

        ContestRepository contestRepository = new ContestRepository(context);

        // when
        List<Contest> result = contestRepository.getAll();

        // then
        assertEquals(10, result.size());
    }
}
