package com.dannykingway.app_badge

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Android app icon badge helper for Kotlin apps.
 *
 * For Android 8.0+ this is driven by notifications; setting the notification
 * number updates launcher badge count on supported launchers.
 */
class AppIconBadgeManager(
    private val context: Context,
    private val smallIconResId: Int,
    private val notificationTitle: CharSequence,
    private val notificationTextProvider: (Int) -> CharSequence
) {

    private val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun updateBadge(count: Int) {
        val safeCount = count.coerceAtLeast(0)

        if (safeCount == 0) {
            notificationManagerCompat.cancel(BADGE_NOTIFICATION_ID)
            return
        }

        if (!canPostNotifications()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ensureBadgeChannel()
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconResId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationTextProvider(safeCount))
            .setNumber(safeCount)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            notificationManagerCompat.notify(BADGE_NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            return
        }
    }

    private fun ensureBadgeChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existingChannel != null) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = CHANNEL_DESCRIPTION
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        if (!notificationManagerCompat.areNotificationsEnabled()) {
            return false
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val CHANNEL_ID = "app_badge_channel"
        private const val CHANNEL_NAME = "App badge"
        private const val CHANNEL_DESCRIPTION = "Badge count notification channel"
        // Keep a stable ID so updates replace the same badge notification.
        private const val BADGE_NOTIFICATION_ID = 9001
    }
}
