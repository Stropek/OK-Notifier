package com.przemolab.oknotifier.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobParameters;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;

import timber.log.Timber;

public class OpenKattisJobService extends JobService {

    private AsyncTask backgroundTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters params) {
        Timber.d("SyncTest: job service started.");

        backgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = OpenKattisJobService.this;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Resources resources = context.getResources();

                String contestsStates = sharedPreferences.getString("pref_contest_switches", resources.getString(R.string.default_contests_states));
                String[] states = contestsStates.split(";");
                boolean approved = Boolean.parseBoolean(states[0]);
                boolean submitted = Boolean.parseBoolean(states[1]);
                boolean rejected = Boolean.parseBoolean(states[2]);

//                boolean ongoingContests = sharedPreferences.getBoolean("pref_upcoming_contests_switch", resources.getBoolean(R.bool.default_ongoing_contests_switch));
//                boolean newProblems = sharedPreferences.getBoolean("pref_new_problems_switch", resources.getBoolean(R.bool.default_new_problems_switch));

                OpenKattisService openKattisService = new OpenKattisService();
                NotifierRepository notifierRepository = new NotifierRepository(getApplicationContext());

                List<Contest> subscribedContests = notifierRepository.getSubscribed();
                for (Contest contest : subscribedContests) {
                    List<Contestant> persistedStandings = notifierRepository.getAllContestants(contest.getContestId());
                    List<Contestant> currentStandings = openKattisService.getContestStandings(contest.getContestId());

                    Timber.d("SyncTest: persisted standings: %s", persistedStandings.size());
                    Timber.d("SyncTest: current standings: %s", currentStandings.size());
                }

//                Timber.d("SyncTest: Just doing a mock job... approved: %s, submitted: %s, failed: %s, contests: %s, problems: %s ", approved, submitted, rejected, ongoingContests, newProblems);
                Timber.d("SyncTest: Just doing a mock job... approved: %s, submitted: %s, failed: %s, contests: %s, problems: %s ", approved, submitted, rejected, false, false);

                // TODO: if notifications are set

                Timber.d("SyncTest: service is working in the background");

//                 10 seconds of working (1000*10ms)
//                for (int i = 0; i < 1000; i++) {
//                    // If the job has been cancelled, stop working; the job will be rescheduled.
//                    if (jobCancelled)
//                        return null;
//
//                    try { Thread.sleep(10); } catch (Exception e) { }
//                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };

        backgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Timber.d("SyncTest: job cancelled before being completed.");

        if (backgroundTask != null)
            backgroundTask.cancel(true);

        return true;
    }
}
