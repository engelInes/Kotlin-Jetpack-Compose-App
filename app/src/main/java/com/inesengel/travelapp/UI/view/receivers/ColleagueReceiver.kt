package com.inesengel.travelapp.UI.view.receivers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.activities.MainActivity
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BROADCAST_TAG
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.PLANTS_ACTION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.RECEIVER_TAG
import com.inesengel.travelapp.UI.view.utils.Constants.Notification.OTHER_APP_NOTIFICATION_DESCRIPTION
import com.inesengel.travelapp.UI.view.utils.Constants.Notification.OTHER_APP_NOTIFICATION_NAME

class ColleagueReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
        Log.d(RECEIVER_TAG, "received")

        if (intent?.action == PLANTS_ACTION) {
            Log.e(BROADCAST_TAG, "Received broadcast from Ana's app.")
        }

        val appContext = context.applicationContext
        createNotificationChannel(appContext)

        val pendingIntent = buildPendingIntent(appContext)
        val notification = buildNotification(appContext, pendingIntent)

        postNotification(appContext, notification)
    }

    private fun buildPendingIntent(context: Context): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildNotification(
        context: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.other_app_broadcast_confirmation_message))
            .setContentText(context.getString(R.string.notification_broadcast_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun postNotification(
        context: Context,
        builder: NotificationCompat.Builder
    ) {
        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(RECEIVER_TAG, "Permission granted. Posting notification.")
                notify(NOTIFICATION_ID, builder.build())
            } else {
                Log.e(RECEIVER_TAG, "Permission denied. Cannot post notification.")
            }
        }
    }

    private fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = OTHER_APP_NOTIFICATION_NAME
            val descriptionText = OTHER_APP_NOTIFICATION_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "colleague_channel"
        const val NOTIFICATION_ID = 2
    }
}
