package com.przemolab.oknotifier.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.activities.ContestActivity
import com.przemolab.oknotifier.data.entries.ContestantEntry
import com.przemolab.oknotifier.services.ContestIntentService

class ContestWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        ContestIntentService.startActionUpdateContestWidgets(context)
    }

    companion object {

        fun updateAppWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, contestant: ContestantEntry) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, contestant)
            }
        }

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, contestant: ContestantEntry) {

            val views = RemoteViews(context.packageName, R.layout.contest_widget_provider)

            views.setTextViewText(R.id.approved_tv, contestant.problemsSolved.toString())
            views.setTextViewText(R.id.submitted_tv, contestant.problemsSubmitted.toString())
            views.setTextViewText(R.id.rejected_tv, contestant.problemsFailed.toString())
            views.setTextViewText(R.id.notTried_tv, contestant.problemsNotTried.toString())

            val intent = Intent(context, ContestActivity::class.java)
            intent.putExtra(Constants.BundleKeys.ContestId, contestant.contestId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.contestWidget_ll, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

