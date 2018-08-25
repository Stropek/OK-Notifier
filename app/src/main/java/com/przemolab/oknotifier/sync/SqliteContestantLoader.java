package com.przemolab.oknotifier.sync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class SqliteContestantLoader extends AsyncTaskLoader<List<Contestant>> {

    private List<Contestant> contestants = null;
    private final NotifierRepository notifierRepository;
    private final OpenKattisService openKattisService;
    private final String contestId;
    private final ContestantsListFragment.OnContestantsListEventListener onContestantsListEventListener;

    public SqliteContestantLoader(Context context, NotifierRepository notifierRepository, OpenKattisService openKattisService, String contestId,
                                  ContestantsListFragment.OnContestantsListEventListener onContestantsListEventListener) {
        super(context);
        this.notifierRepository = notifierRepository;
        this.openKattisService = openKattisService;
        this.contestId = contestId;
        this.onContestantsListEventListener = onContestantsListEventListener;
    }

    @Override
    public List<Contestant> loadInBackground() {
        try {
            Timber.d("Loading contestants from SQLite");
            List<Contestant> contestants = notifierRepository.getAllContestants(contestId);
            if (contestants.isEmpty()) {
                Timber.d("No contestants in SQLite. Loading from Open Kattis Service");
                contestants = openKattisService.getContestStandings(contestId);
                notifierRepository.persistContestants(contestId, contestants);
            }
            return contestants;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        onContestantsListEventListener.onSyncStarted();
        if (contestants == null) {
            forceLoad();
        } else {
            deliverResult(contestants);
        }
    }

    @Override
    public void deliverResult(List<Contestant> data) {
        contestants = data;
        onContestantsListEventListener.onSyncFinished(data, false);
        super.deliverResult(data);
    }
}