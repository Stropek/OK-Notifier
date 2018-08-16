package com.przemolab.oknotifier.asyncTasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;

import java.util.List;

import timber.log.Timber;

public class SqliteContestantLoader extends AsyncTaskLoader<List<Contestant>> {

    private List<Contestant> contestants = null;
    private NotifierRepository notifierRepository;
    private String contestId;

    public SqliteContestantLoader(Context context, NotifierRepository notifierRepository, String contestId) {
        super(context);
        this.notifierRepository = notifierRepository;
        this.contestId = contestId;
    }

    @Override
    public List<Contestant> loadInBackground() {
        try {
            Timber.d("Loading contestants from SQLite");
            return notifierRepository.getAllContestants(contestId);
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
