package com.przemolab.oknotifier.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.activities.ContestActivity
import android.graphics.drawable.BitmapDrawable
import com.przemolab.oknotifier.data.entries.ContestEntry

object NotificationUtils {

    private const val CONTEST_PENDING_INTENT_ID = 1
    private const val CONTEST_NOTIFICATION_ID = 100

    private const val CONTEST_NOTIFICATION_CHANNEL_ID = "contest_notification_channel"

    fun notifyAboutContestUpdates(context: Context, contestEntry: ContestEntry, newSubmissions: List<String>) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CONTEST_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.contest_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, CONTEST_NOTIFICATION_CHANNEL_ID)
                .setColorized(true)
                .setColor(ContextCompat.getColor(context, R.color.darkGreen))
                .setSmallIcon(R.drawable.ic_add)
                .setLargeIcon(notificationIcon(context))
                .setContentTitle(contestEntry.name)
                .setContentText(String.format("%s new updates!", newSubmissions.size))
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(formatSubmissions(newSubmissions))
                )
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, contestEntry.contestId))
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }

        notificationManager.notify(CONTEST_NOTIFICATION_ID + contestEntry.id, notificationBuilder.build())
    }

    private fun formatSubmissions(newSubmissions: List<String>): String {
        val text = StringBuilder()
        for (submission in newSubmissions) {
            text.append(submission).append("\n")
        }
        return text.toString()
    }

    private fun contentIntent(context: Context, contestId: String): PendingIntent {
        val contestActivityIntent = Intent(context, ContestActivity::class.java)
        contestActivityIntent.putExtra(Constants.BundleKeys.ContestId, contestId)

        return PendingIntent.getActivity(context, CONTEST_PENDING_INTENT_ID, contestActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun notificationIcon(context: Context): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_add)

        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}
