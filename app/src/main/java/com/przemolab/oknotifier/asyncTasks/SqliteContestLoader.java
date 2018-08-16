package com.przemolab.oknotifier.asyncTasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.NotifierRepository;

import java.util.List;

import timber.log.Timber;

public class SqliteContestLoader extends AsyncTaskLoader<List<Contest>> {

    private List<Contest> contests = null;
    private NotifierRepository notifierRepository;
    private SortOrder sortOrder;

    public SqliteContestLoader(Context context, NotifierRepository notifierRepository, SortOrder sortOrder) {
        super(context);
        this.notifierRepository = notifierRepository;
        this.sortOrder = sortOrder;
    }

    @Override
    public List<Contest> loadInBackground() {
        try {
            Timber.d("Loading contests from SQLite");
            return notifierRepository.getAll(sortOrder);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (contests == null) {
            forceLoad();
        } else {
            deliverResult(contests);
        }
    }

    @Override
    public void deliverResult(List<Contest> data) {
        contests = data;
        super.deliverResult(data);
    }
}
