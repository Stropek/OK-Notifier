package com.przemolab.oknotifier.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.activities.ContestActivity;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.services.ContestIntentService;

public class ContestWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Contestant contestant) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.contest_widget_provider);

        views.setTextViewText(R.id.approved_tv, String.valueOf(contestant.getProblemsSolved()));
        views.setTextViewText(R.id.submitted_tv, String.valueOf(contestant.getProblemsSubmitted()));
        views.setTextViewText(R.id.rejected_tv, String.valueOf(contestant.getProblemsFailed()));
        views.setTextViewText(R.id.notTried_tv, String.valueOf(contestant.getProblemsNotTried()));

        Intent intent = new Intent(context, ContestActivity.class);
        intent.putExtra(Constants.BundleKeys.ContestId, contestant.getContestId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.contestWidget_ll, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Contestant contestant) {
        for(int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, contestant);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ContestIntentService.startActionUpdateContestWidgets(context);
    }
}

