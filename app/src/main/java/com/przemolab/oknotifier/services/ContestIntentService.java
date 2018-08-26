package com.przemolab.oknotifier.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.widgets.ContestWidgetDataProvider;
import com.przemolab.oknotifier.widgets.ContestWidgetProvider;

public class ContestIntentService extends IntentService {

    public static String ACTION_UPDATE_CONTESTS_WIDGETS = "update_contests_widgets";

    public ContestIntentService() {
        super(ContestIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_CONTESTS_WIDGETS.equals(action)) {
                handleActionUpdateRecipesWidgets();
            }
        }
    }

    public static void startActionUpdateContestWidgets(Context context) {
        Intent intent = new Intent(context, ContestIntentService.class);
        intent.setAction(ACTION_UPDATE_CONTESTS_WIDGETS);
        context.startService(intent);
    }

    private void handleActionUpdateRecipesWidgets() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferences.Name, Context.MODE_PRIVATE);
        ContestWidgetDataProvider dataProvider = new ContestWidgetDataProvider(sharedPreferences);
        Contestant bestContestant = dataProvider.getBestContestant();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ContestWidgetProvider.class));

        // update all widgets
        ContestWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds, bestContestant);
    }
}
