package com.przemolab.oknotifier.modules;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.przemolab.oknotifier.data.ContestContract;
import com.przemolab.oknotifier.models.Contest;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ContestRepository {

    private Context context;

    public ContestRepository(Context context) {
        this.context = context;
    }

    public List<Contest> getAll() {
        try {
            List<Contest> contests = new ArrayList<>();
            Uri contestsUri = ContestContract.ContestEntry.CONTENT_URI;
            Cursor cursor = context.getContentResolver()
                    .query(contestsUri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contests.add(Contest.getFromCursor(cursor));
                }

                cursor.close();
            }

            return contests;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }
}
