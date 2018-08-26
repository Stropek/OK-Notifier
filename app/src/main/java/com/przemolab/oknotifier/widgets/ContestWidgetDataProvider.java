package com.przemolab.oknotifier.widgets;

import android.content.Context;
import android.content.SharedPreferences;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.models.Contestant;

public class ContestWidgetDataProvider {

    private SharedPreferences sharedPreferences;

    public ContestWidgetDataProvider(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void toggleSource(Context context, Contestant contestant, boolean setAsSource) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SharedPreferences.BestContestant);

        if (setAsSource) {
            editor.putString(Constants.SharedPreferences.BestContestant, contestant.getSharedPreferencesValue());
        }

        editor.apply();

//        ContestIntentService.startActionUpdateContestWidgets(context);
    }

    public boolean isCurrentSource(String contestId) {
        String currentBestContestant = sharedPreferences.getString(Constants.SharedPreferences.BestContestant, null);
        if (currentBestContestant == null)
            return false;

        String currentContestId = currentBestContestant.split(";")[0];
        return currentContestId.equals(contestId);
    }
}
