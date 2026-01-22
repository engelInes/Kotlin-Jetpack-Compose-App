package com.inesengel.travelapp.UI.view.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.CANCEL_DELAY_COMMAND
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.START_DELAY_COMMAND
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.USER_TRACKING_COMMAND

class UserActivityService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val notificationRunnable = Runnable {
        postNotification()
        stopSelf()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        createNotificationChannel()
        val command = intent?.getStringExtra(USER_TRACKING_COMMAND)

        when (command) {
            START_DELAY_COMMAND -> {
                handler.postDelayed(notificationRunnable, DELAY)
            }

            CANCEL_DELAY_COMMAND -> {
                handler.removeCallbacks(notificationRunnable)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        handler.removeCallbacks(notificationRunnable)
        super.onDestroy()
    }

    private fun postNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.check_circle_24px)
            .setContentTitle(getString(R.string.come_back_notification_message))
            .setContentText(getString(R.string.user_inactivity_message))
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATIONS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private companion object {
        const val DELAY = 30000L
        const val CHANNEL_ID = "user_inactivity_channel"
        const val NOTIFICATIONS_CHANNEL_NAME = "Inactivity Notifications"
        const val NOTIFICATION_ID = 1
    }
}