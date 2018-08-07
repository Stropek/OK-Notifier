package com.przemolab.oknotifier.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.ContestRepository;

import java.util.List;

import timber.log.Timber;

public class ContestLoader extends AsyncTaskLoader<List<Contest>> {

    private List<Contest> contests = null;
    private ContestRepository contestRepository;

    public ContestLoader(Context context, ContestRepository contestRepository) {
        super(context);
        this.contestRepository = contestRepository;
    }

    @Override
    public List<Contest> loadInBackground() {
        try {
            Timber.d("Loading contests from SQLite");
            return contestRepository.getAll();
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
