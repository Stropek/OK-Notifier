package com.przemolab.oknotifier.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobParameters;
import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;
import com.przemolab.oknotifier.utils.NotificationUtils;

import java.util.ArrayList;
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

                SyncSubscribedContests(context, sharedPreferences, resources);

                // TODO: implement ongoingContests / newProblems
//                boolean ongoingContests = sharedPreferences.getBoolean("pref_upcoming_contests_switch", resources.getBoolean(R.bool.default_ongoing_contests_switch));
//                boolean newProblems = sharedPreferences.getBoolean("pref_new_problems_switch", resources.getBoolean(R.bool.default_new_problems_switch));

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

    private void SyncSubscribedContests(Context context, SharedPreferences sharedPreferences, Resources resources) {
        String contestsStates = sharedPreferences.getString(Constants.SharedPreferences.INSTANCE.getContestSwitches(), resources.getString(R.string.default_contests_states));
        String[] states = contestsStates.split(";");
        boolean approved = Boolean.parseBoolean(states[0]);
        boolean submitted = Boolean.parseBoolean(states[1]);
        boolean rejected = Boolean.parseBoolean(states[2]);

        if (approved || submitted || rejected) {
            OpenKattisService openKattisService = new OpenKattisService();
            NotifierRepository notifierRepository = new NotifierRepository(getApplicationContext());

            List<Contest> subscribedContests = notifierRepository.getSubscribed();
            for (Contest contest : subscribedContests) {
                List<Contestant> persistedStandings = notifierRepository.getAllContestants(contest.getContestId());
                List<Contestant> currentStandings = openKattisService.getContestStandings(contest.getContestId());

                List<String> newSubmissions = getNewSubmissions(persistedStandings, currentStandings, approved, submitted, rejected);
                if (!newSubmissions.isEmpty()) {
                    NotificationUtils.notifyAboutContestUpdates(context, contest, newSubmissions);

                    // persist current contest standings to prevent the same notifications from recurring
                    notifierRepository.persistContestants(contest.getContestId(), currentStandings);
                }
            }
        }
    }

    private List<String> getNewSubmissions(List<Contestant> persisted, List<Contestant> current, boolean approved, boolean submitted, boolean rejected) {
        List<String> submissions = new ArrayList<>();

        for (Contestant contestant: current) {
            for (Contestant persistedContestant: persisted) {
                if (contestant.getName().equals(persistedContestant.getName())) {
                    if (approved && contestant.getProblemsSolved() > persistedContestant.getProblemsSolved()) {
                        submissions.add(String.format("%s solved %s new problems!!! :D",
                                contestant.getName(), contestant.getProblemsSolved() - persistedContestant.getProblemsSolved()));
                    }
                    if (submitted && contestant.getProblemsSubmitted() > persistedContestant.getProblemsSubmitted()) {
                        submissions.add(String.format("%s submitted %s new problems",
                                contestant.getName(), contestant.getProblemsSubmitted() - persistedContestant.getProblemsSubmitted()));
                    }
                    if (rejected && contestant.getProblemsFailed() > persistedContestant.getProblemsFailed()) {
                        submissions.add(String.format("%s failed to solve %s problems :(",
                                contestant.getName(), contestant.getProblemsFailed() - persistedContestant.getProblemsFailed()));
                    }
                }
            }
        }
        return submissions;
    }
}
