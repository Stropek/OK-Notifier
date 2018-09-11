package com.przemolab.oknotifier.services

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.widgets.ContestWidgetDataProvider
import com.przemolab.oknotifier.widgets.ContestWidgetProvider

class ContestIntentService : IntentService(ContestIntentService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_UPDATE_CONTESTS_WIDGETS == action) {
                handleActionUpdateRecipesWidgets()
            }
        }
    }

    private fun handleActionUpdateRecipesWidgets() {
        val sharedPreferences = getSharedPreferences(Constants.SharedPreferences.Name, Context.MODE_PRIVATE)
        val dataProvider = ContestWidgetDataProvider(sharedPreferences)
        val bestContestant = dataProvider.bestContestant

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, ContestWidgetProvider::class.java))

        // update all widgets
        ContestWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds, bestContestant)
    }

    companion object {

        var ACTION_UPDATE_CONTESTS_WIDGETS = "update_contests_widgets"

        fun startActionUpdateContestWidgets(context: Context) {
            val intent = Intent(context, ContestIntentService::class.java)
            intent.action = ACTION_UPDATE_CONTESTS_WIDGETS
            context.startService(intent)
        }
    }
}
