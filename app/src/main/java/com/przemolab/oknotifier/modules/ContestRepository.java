package com.przemolab.oknotifier.modules;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.przemolab.oknotifier.data.ContestContract;
import com.przemolab.oknotifier.models.Contest;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void persist(List<Contest> contests) {
        try {
            List<Contest> persistedContests = getAll();
            List<Contest> updatedContests = new ArrayList<>();

            for (Contest contest : contests) {
                boolean exists = false;

                for (Contest persistedContest : persistedContests) {
                    if (persistedContest.getId().equals(contest.getId())) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    updateContest(contest);
                    updatedContests.add(contest);
                } else {
                    createContest(contest);
                }
            }

            List<Contest> contestsToDelete = new ArrayList<>();
            for (Contest persistedContest: persistedContests) {
                boolean toDelete = true;

                for (Contest updatedContest: updatedContests) {
                    if (persistedContest.getId().equals(updatedContest.getId())) {
                        toDelete = false;
                        break;
                    }
                }

                if (toDelete) {
                    contestsToDelete.add(persistedContest);
                }
            }

            if (contestsToDelete.size() > 0) {
                deleteContests(contestsToDelete);
            }

        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private void deleteContests(List<Contest> contestsToDelete) {
        Uri deleteUri = ContestContract.ContestEntry.CONTENT_URI
                .buildUpon().appendPath("byContestIds").build();

        String[] contestsIds = new String[contestsToDelete.size()];
        for (int i = 0; i < contestsToDelete.size(); i++) {
            contestsIds[i] = contestsToDelete.get(i).getId();
        }

        Timber.i("Deleting contests: %s", Arrays.toString(contestsIds));
        context.getContentResolver().delete(deleteUri, null, contestsIds);
    }

    private void createContest(Contest contest) {
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

        Timber.i("Creating contest: %s [%s]", contest.getName(), contest.getId());
        context.getContentResolver().insert(uri, contest.toContentValues());
    }

    private void updateContest(Contest contest) {
        Uri uri = ContestContract.ContestEntry.CONTENT_URI;

        Timber.i("Updating contest: %s [%s]", contest.getName(), contest.getId());
        context.getContentResolver().update(uri, contest.toContentValues(),
                ContestContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                new String[] {contest.getId()});
    }
}
