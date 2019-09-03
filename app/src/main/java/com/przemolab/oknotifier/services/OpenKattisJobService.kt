package com.przemolab.oknotifier.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.AsyncTask
import android.preference.PreferenceManager

import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.JobParameters
import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.data.entries.ContestantEntry
import com.przemolab.oknotifier.modules.NotifierRepository
import com.przemolab.oknotifier.modules.OpenKattisService
import com.przemolab.oknotifier.utils.NotificationUtils

import java.util.ArrayList

import timber.log.Timber

class OpenKattisJobService : JobService() {

    private var backgroundTask: AsyncTask<*, *, *>? = null

    @SuppressLint("StaticFieldLeak")
    override fun onStartJob(params: JobParameters): Boolean {
        Timber.d("Sync: job service started.")

        backgroundTask = object : AsyncTask<Any, Any, Any>() {
            override fun doInBackground(objects: Array<Any>): Any? {
                val context = this@OpenKattisJobService
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val resources = context.resources

                syncSubscribedContests(context, sharedPreferences, resources)

                // TODO: implement ongoingContests / newProblems
                //                boolean ongoingContests = sharedPreferences.getBoolean("pref_upcoming_contests_switch", resources.getBoolean(R.bool.default_ongoing_contests_switch));
                //                boolean newProblems = sharedPreferences.getBoolean("pref_new_problems_switch", resources.getBoolean(R.bool.default_new_problems_switch));

                return null
            }

            override fun onPostExecute(o: Any?) {
                jobFinished(params, false)
            }
        }

        backgroundTask!!.execute()
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Timber.d("Sync: job cancelled before being completed.")

        if (backgroundTask != null)
            backgroundTask!!.cancel(true)

        return true
    }

    private fun syncSubscribedContests(context: Context, sharedPreferences: SharedPreferences, resources: Resources) {
        val contestsStates = sharedPreferences.getString(Constants.SharedPreferences.ContestSwitches, resources.getString(R.string.default_contests_states))
        val states = contestsStates!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val approved = java.lang.Boolean.parseBoolean(states[0])
        val submitted = java.lang.Boolean.parseBoolean(states[1])
        val rejected = java.lang.Boolean.parseBoolean(states[2])

        if (approved || submitted || rejected) {
            val openKattisService = OpenKattisService()
            val notifierRepository = NotifierRepository(applicationContext)

            val subscribedContests = notifierRepository.subscribed
            for (contest in subscribedContests!!) {
                val persistedStandings = notifierRepository.getAllContestants(contest.contestId)
                val currentStandings = openKattisService.getContestStandings(contest.contestId)

                val newSubmissions = getNewSubmissions(persistedStandings, currentStandings!!, approved, submitted, rejected)
                if (!newSubmissions.isEmpty()) {
                    NotificationUtils.notifyAboutContestUpdates(context, contest, newSubmissions)

                    // persist current contest standings to prevent the same notifications from recurring
                    notifierRepository.persistContestants(contest.contestId, currentStandings)
                }
            }
        }
    }

    private fun getNewSubmissions(persisted: List<ContestantEntry>?, current: List<ContestantEntry>, approved: Boolean, submitted: Boolean, rejected: Boolean): List<String> {
        val submissions = ArrayList<String>()

        for (contestant in current) {
            val persistedContestant = persisted!!.find { it -> it.name == contestant.name }

            val newSolved =
                    if (persistedContestant == null) contestant.problemsSolved
                    else contestant.problemsSolved - persistedContestant.problemsSolved
            val newSubmitted =
                    if (persistedContestant == null) contestant.problemsSubmitted
                    else contestant.problemsSubmitted - persistedContestant.problemsSubmitted
            val newFailed =
                    if (persistedContestant == null) contestant.problemsFailed
                    else contestant.problemsFailed - persistedContestant.problemsFailed

            if (approved && newSolved > 0) {
                submissions.add(String.format("%s solved %s new problems!!! :D", contestant.name, newSolved))
            }
            if (submitted && newSubmitted > 0) {
                submissions.add(String.format("%s submitted %s new problems", contestant.name, newSubmitted))
            }
            if (rejected && newFailed > 0) {
                submissions.add(String.format("%s failed to solve %s problems :(", contestant.name, newFailed))
            }
        }
        return submissions
    }
}
