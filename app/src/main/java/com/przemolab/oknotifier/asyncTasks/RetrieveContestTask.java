package com.przemolab.oknotifier.asyncTasks;

import android.os.AsyncTask;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class RetrieveContestTask extends AsyncTask<Void, Void, List<Contest>> {

    private OpenKattisService openKattisService;

    public RetrieveContestTask(OpenKattisService openKattisService) {
        this.openKattisService = openKattisService;
    }

    @Override
    protected List<Contest> doInBackground(Void...voids) {
        try {
            return openKattisService.getOngoingContests();
        } catch (Exception ex) {
            Timber.e(ex);
        }
        return null;
    }
}
