package com.przemolab.oknotifier.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.activities.ContestActivity;
import com.przemolab.oknotifier.models.Contest;

import java.util.List;

public class NotificationUtils {

    private final static int CONTEST_PENDING_INTENT_ID = 1;
    private final static int CONTEST_NOTIFICATION_ID = 100;

    private final static String CONTEST_NOTIFICATION_CHANNEL_ID = "contest_notification_channel";

    public static void notifyAboutContestUpdates(Context context, Contest contest, List<String> newSubmissions) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CONTEST_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.contest_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CONTEST_NOTIFICATION_CHANNEL_ID)
                        .setColorized(true)
                        .setColor(ContextCompat.getColor(context, R.color.darkGreen))
                        .setSmallIcon(R.drawable.ic_add)
                        .setLargeIcon(notificationIcon(context))
                        .setContentTitle(contest.getName())
                        .setContentText(String.format("%s new updates!", newSubmissions.size()))
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(formatSubmissions(newSubmissions))
                        )
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context, contest.getContestId()))
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        assert notificationManager != null;
        notificationManager.notify(CONTEST_NOTIFICATION_ID + contest.getId(), notificationBuilder.build());
    }

    private static String formatSubmissions(List<String> newSubmissions) {
        StringBuilder text = new StringBuilder();
        for (String submission : newSubmissions) {
            text.append(submission).append("\n");
        }
        return text.toString();
    }

    private static PendingIntent contentIntent(Context context, String contestId) {
        Intent contestActivityIntent = new Intent(context, ContestActivity.class);
        contestActivityIntent.putExtra(Constants.BundleKeys.ContestId, contestId);

        return PendingIntent.getActivity(context, CONTEST_PENDING_INTENT_ID, contestActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap notificationIcon(Context context) {
        Resources resources = context.getResources();
        // TODO: change icon
        return BitmapFactory.decodeResource(resources, R.drawable.ic_add);
    }
}
