package com.przemolab.oknotifier.sync;

import android.os.AsyncTask;

import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class RetrieveContestantsTask extends AsyncTask<Void, Void, List<Contestant>> {

    private OpenKattisService openKattisService;
    private NotifierRepository notifierRepository;
    private String contestId;
    private ContestantsListFragment.OnContestantsListEventListener onContestantsListEventListener;

    public RetrieveContestantsTask(OpenKattisService openKattisService, NotifierRepository notifierRepository, String contestId,
                                   ContestantsListFragment.OnContestantsListEventListener onContestantsListEventListener) {
        this.openKattisService = openKattisService;
        this.notifierRepository = notifierRepository;
        this.contestId = contestId;
        this.onContestantsListEventListener = onContestantsListEventListener;
    }

    @Override
    protected void onPreExecute() {
        onContestantsListEventListener.onSyncStarted();
    }

    @Override
    protected List<Contestant> doInBackground(Void...voids) {
        try {
            List<Contestant> contestants = openKattisService.getContestStandings(contestId);
            notifierRepository.persistContestants(contestId, contestants);
            return contestants;
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Contestant> contestants) {
        onContestantsListEventListener.onSyncFinished(contestants, true);
    }
}
