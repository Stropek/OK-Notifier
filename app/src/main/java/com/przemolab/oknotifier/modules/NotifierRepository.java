package com.przemolab.oknotifier.modules;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.przemolab.oknotifier.data.NotifierContract;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class NotifierRepository {

    private Context context;

    public NotifierRepository(Context context) {
        this.context = context;
    }

    public List<Contest> getAll(SortOrder sortOrder) {
        try {
            List<Contest> contests = new ArrayList<>();
            Uri contestsUri = NotifierContract.ContestEntry.CONTENT_URI;
            Cursor cursor = context.getContentResolver()
                    .query(contestsUri, null, null, null, getSortOrder(sortOrder));

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

    public List<Contestant> getAllContestants(String contestId) {
        try {
            List<Contestant> contestants = new ArrayList<>();
            Uri contestantsUri = NotifierContract.ContestantEntry.CONTENT_URI.buildUpon()
                    .appendPath("byContestId")
                    .appendPath(contestId)
                    .build();
            Cursor cursor = context.getContentResolver()
                    .query(contestantsUri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contestants.add(Contestant.getFromCursor(cursor));
                }

                cursor.close();
            }

            return contestants;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    public void persist(List<Contest> contests) {
        try {
            List<Contest> persistedContests = getAll(SortOrder.SubscribedFirst);
            List<Contest> updatedContests = new ArrayList<>();

            for (Contest contest : contests) {
                boolean exists = false;

                for (Contest persistedContest : persistedContests) {
                    if (persistedContest.getId().equals(contest.getId())) {
                        contest.setSubscribed(persistedContest.isSubscribed());
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

    public void updateContest(Contest contest) {
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        Timber.i("Updating contest: %s [%s]", contest.getName(), contest.getId());
        context.getContentResolver().update(uri, contest.toContentValues(),
                NotifierContract.ContestEntry.COLUMN_CONTEST_ID + "=?",
                new String[] {contest.getId()});
    }

    private void deleteContests(List<Contest> contestsToDelete) {
        Uri deleteUri = NotifierContract.ContestEntry.CONTENT_URI
                .buildUpon().appendPath("byContestIds").build();

        String[] contestsIds = new String[contestsToDelete.size()];
        for (int i = 0; i < contestsToDelete.size(); i++) {
            contestsIds[i] = contestsToDelete.get(i).getId();
        }

        Timber.i("Deleting contests: %s", Arrays.toString(contestsIds));
        context.getContentResolver().delete(deleteUri, null, contestsIds);
    }

    private void createContest(Contest contest) {
        Uri uri = NotifierContract.ContestEntry.CONTENT_URI;

        Timber.i("Creating contest: %s [%s]", contest.getName(), contest.getId());
        context.getContentResolver().insert(uri, contest.toContentValues());
    }

    private String getSortOrder(SortOrder sortOrder) {
        String orderBy = NotifierContract.ContestEntry.COLUMN_IS_SUBSCRIBED + " DESC";
        switch (sortOrder) {
            case ByName:
                orderBy = NotifierContract.ContestEntry.COLUMN_NAME;
                break;
            case ByStartDate:
                orderBy = NotifierContract.ContestEntry.COLUMN_START_DATE;
                break;
            case ByNumberOfProblems:
                orderBy = NotifierContract.ContestEntry.COLUMN_NUM_OF_PROBLEMS + " DESC";
                break;
            case ByNumberOfContestants:
                orderBy = NotifierContract.ContestEntry.COLUMN_NUM_OF_CONTESTANTS + " DESC";
                break;
        }

        return orderBy;
    }
}