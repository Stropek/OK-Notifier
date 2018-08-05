package com.przemolab.oknotifier.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.services.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class ContestLoader extends AsyncTaskLoader<List<Contest>> {

    private List<Contest> contests = null;
    private OpenKattisService openKattisService;

    public ContestLoader(Context context, OpenKattisService openKattisService) {
        super(context);
        this.openKattisService = openKattisService;
    }

    @Override
    public List<Contest> loadInBackground() {
        try {
            Timber.d("Loading contests...");
            return openKattisService.getOngoingContests();
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
