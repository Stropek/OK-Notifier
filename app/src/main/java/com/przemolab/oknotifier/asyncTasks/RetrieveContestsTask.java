package com.przemolab.oknotifier.asyncTasks;

import android.os.AsyncTask;

import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class RetrieveContestsTask extends AsyncTask<Void, Void, List<Contest>> {

    private OpenKattisService openKattisService;
    private NotifierRepository notifierRepository;
    private ContestsListFragment.OnContestsListEventsListener onContestsListEventsListener;

    public RetrieveContestsTask(OpenKattisService openKattisService, NotifierRepository notifierRepository,
                               ContestsListFragment.OnContestsListEventsListener onContestsListEventsListener) {
        this.openKattisService = openKattisService;
        this.notifierRepository = notifierRepository;
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
            notifierRepository.persist(contests);
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
