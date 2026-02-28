package com.sleepguardian.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sleepguardian.features.active_alarm.ActiveAlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        val vibrate = intent.getBooleanExtra(EXTRA_VIBRATE, true)
        val ringtoneUri = intent.getStringExtra(EXTRA_RINGTONE_URI)
        val label = intent.getStringExtra(EXTRA_LABEL)
        val snoozeEnabled = intent.getBooleanExtra(EXTRA_SNOOZE_ENABLED, true)
        val taskType = intent.getStringExtra(EXTRA_TASK_TYPE) ?: "MATH"
        val taskDifficulty = intent.getStringExtra(EXTRA_TASK_DIFFICULTY) ?: "MEDIUM"

        // Start the foreground service for sound/vibration
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_VIBRATE, vibrate)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
            putExtra(EXTRA_LABEL, label)
            putExtra(EXTRA_SNOOZE_ENABLED, snoozeEnabled)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Launch the full-screen alarm activity directly.
        // setAlarmClock() grants a temporary background activity start exemption.
        val activityIntent = Intent(context, ActiveAlarmActivity::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_LABEL, label)
            putExtra(EXTRA_SNOOZE_ENABLED, snoozeEnabled)
            putExtra(EXTRA_TASK_TYPE, taskType)
            putExtra(EXTRA_TASK_DIFFICULTY, taskDifficulty)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        context.startActivity(activityIntent)
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_VIBRATE = "extra_vibrate"
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_SNOOZE_ENABLED = "extra_snooze_enabled"
        const val EXTRA_TASK_TYPE = "extra_task_type"
        const val EXTRA_TASK_DIFFICULTY = "extra_task_difficulty"
    }
}
