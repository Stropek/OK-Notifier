package com.przemolab.oknotifier.asyncTasks;

import android.os.AsyncTask;

import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class RetrieveContestsTask extends AsyncTask<Void, Void, List<Contest>> {

    private OpenKattisService openKattisService;
    private ContestRepository contestRepository;
    private ContestsListFragment.OnContestsListEventsListener onContestsListEventsListener;

    public RetrieveContestsTask(OpenKattisService openKattisService, ContestRepository contestRepository,
                               ContestsListFragment.OnContestsListEventsListener onContestsListEventsListener) {
        this.openKattisService = openKattisService;
        this.contestRepository = contestRepository;
        this.onContestsListEventsListener = onContestsListEventsListener;
    }

    @Override
    protected void onPreExecute() {
        onContestsListEventsListener.onSyncStarted();
    }

    @Override
    protected List<Contest> doInBackground(Void...voids) {
        try {
            List<Contest> contests = openKattisService.getOngoingContests();
            contestRepository.persist(contests);
            return contests;
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Contest> contests) {
        onContestsListEventsListener.onSyncFinished(contests);
    }
}
