package com.przemolab.oknotifier.widgets;

import android.content.Context;
import android.content.SharedPreferences;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.services.ContestIntentService;

public class ContestWidgetDataProvider {

    private SharedPreferences sharedPreferences;

    public ContestWidgetDataProvider(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Contestant getBestContestant() {
        Contestant bestContestant = new Contestant("-", "-", 0, 0, 0, 0, 0);
        String bestContestantString = sharedPreferences.getString(Constants.SharedPreferences.BestContestant, null);

        if (bestContestantString != null) {
            String[] bestContestantValues = bestContestantString.split(";");

            bestContestant.setContestId(bestContestantValues[0]);
            bestContestant.setProblemsSolved(Integer.valueOf(bestContestantValues[1]));
            bestContestant.setProblemsSubmitted(Integer.valueOf(bestContestantValues[2]));
            bestContestant.setProblemsFailed(Integer.valueOf(bestContestantValues[3]));
            bestContestant.setProblemsNotTried(Integer.valueOf(bestContestantValues[4]));
        }

        return bestContestant;
    }

    public void toggleSource(Context context, Contestant contestant, boolean setAsSource) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SharedPreferences.BestContestant);

        if (setAsSource) {
            editor.putString(Constants.SharedPreferences.BestContestant, contestant.getSharedPreferencesValue());
        }

        editor.apply();

        ContestIntentService.startActionUpdateContestWidgets(context);
    }

    public boolean isCurrentSource(String contestId) {
        String currentBestContestant = sharedPreferences.getString(Constants.SharedPreferences.BestContestant, null);
        if (currentBestContestant == null)
            return false;

        String currentContestId = currentBestContestant.split(";")[0];
        return currentContestId.equals(contestId);
    }
}
