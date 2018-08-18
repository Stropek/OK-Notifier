package com.przemolab.oknotifier.asyncTasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

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

    public SqliteContestantLoader(Context context, NotifierRepository notifierRepository, OpenKattisService openKattisService, String contestId) {
        super(context);
        this.notifierRepository = notifierRepository;
        this.openKattisService = openKattisService;
        this.contestId = contestId;
    }

    @Override
    public List<Contestant> loadInBackground() {
        try {
            Timber.d("Loading contestants from SQLite");
            List<Contestant> contestants = notifierRepository.getAllContestants(contestId);
            if (contestants.isEmpty()) {
                Timber.d("No contestants in SQLite. Loading from Open Kattis Service");
                contestants = openKattisService.getContestStandings(contestId);
            }
            return contestants;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (contestants == null) {
            forceLoad();
        } else {
            deliverResult(contestants);
        }
    }

    @Override
    public void deliverResult(List<Contestant> data) {
        contestants = data;
        super.deliverResult(data);
    }
}
