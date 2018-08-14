package com.przemolab.oknotifier.asyncTasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.ContestRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SqliteContestantLoader extends AsyncTaskLoader<List<Contestant>> {

    private List<Contestant> contestants = null;
//    private ContestRepository contestRepository;

    public SqliteContestantLoader(Context context) {
        super(context);
//        this.contestRepository = contestRepository;
    }

    @Override
    public List<Contestant> loadInBackground() {
        try {
            Timber.d("Loading contestants from SQLite");

            // TODO:
            return new ArrayList<>();
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
