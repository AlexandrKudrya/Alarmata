package com.sleepguardian.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sleepguardian.R
import com.sleepguardian.core.alarm.AlarmReceiver
import com.sleepguardian.features.active_alarm.ActiveAlarmActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active alarm notifications"
                setBypassDnd(true)
                setSound(null, null) // Sound is played by AlarmService
                enableVibration(false) // Vibration is handled by AlarmService
            }
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }

    fun buildAlarmNotification(
        alarmId: Long,
        label: String?,
        snoozeEnabled: Boolean
    ): NotificationCompat.Builder {
        val fullScreenIntent = Intent(context, ActiveAlarmActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmReceiver.EXTRA_LABEL, label)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, snoozeEnabled)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPi = PendingIntent.getActivity(
            context, alarmId.toInt(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        val dismissPi = PendingIntent.getBroadcast(
            context, (alarmId.toInt() * 10) + 1, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(label ?: context.getString(R.string.alarm_notification_title))
            .setContentText(context.getString(R.string.alarm_notification_text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPi, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(0, context.getString(R.string.dismiss), dismissPi)

        if (snoozeEnabled) {
            val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
                action = ACTION_SNOOZE
                putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            }
            val snoozePi = PendingIntent.getBroadcast(
                context, (alarmId.toInt() * 10) + 2, snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, context.getString(R.string.snooze_label), snoozePi)
        }

        return builder
    }

    fun cancelNotification(alarmId: Long) {
        notificationManager.cancel(alarmId.toInt())
    }

    companion object {
        const val ALARM_CHANNEL_ID = "alarm_channel"
        const val ACTION_DISMISS = "com.sleepguardian.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.sleepguardian.ACTION_SNOOZE"
    }
}
