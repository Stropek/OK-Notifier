package com.przemolab.oknotifier.widgets

import android.content.Context
import android.content.SharedPreferences

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.models.Contestant
import com.przemolab.oknotifier.services.ContestIntentService

class ContestWidgetDataProvider(private val sharedPreferences: SharedPreferences) {

    val bestContestant: Contestant
        get() {
            val bestContestant = Contestant("-", "-", 0, 0, 0, 0, 0)
            val bestContestantString = sharedPreferences.getString(Constants.SharedPreferences.BestContestant, null)

            if (bestContestantString != null) {
                val bestContestantValues = bestContestantString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                bestContestant.contestId = bestContestantValues[0]
                bestContestant.problemsSolved = Integer.valueOf(bestContestantValues[1])
                bestContestant.problemsSubmitted = Integer.valueOf(bestContestantValues[2])
                bestContestant.problemsFailed = Integer.valueOf(bestContestantValues[3])
                bestContestant.problemsNotTried = Integer.valueOf(bestContestantValues[4])
            }

            return bestContestant
        }

    fun toggleSource(context: Context, contestant: Contestant, setAsSource: Boolean) {
        val editor = sharedPreferences.edit()
        editor.remove(Constants.SharedPreferences.BestContestant)

        if (setAsSource) {
            editor.putString(Constants.SharedPreferences.BestContestant, contestant.sharedPreferencesValue)
        }

        editor.apply()

        ContestIntentService.startActionUpdateContestWidgets(context)
    }

    fun isCurrentSource(contestId: String): Boolean {
        val currentBestContestant = sharedPreferences.getString(Constants.SharedPreferences.BestContestant, null)
                ?: return false

        val currentContestId = currentBestContestant.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        return currentContestId == contestId
    }
}
