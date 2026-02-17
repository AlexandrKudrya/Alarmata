package com.sleepguardian.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_VIBRATE, intent.getBooleanExtra(EXTRA_VIBRATE, true))
            putExtra(EXTRA_RINGTONE_URI, intent.getStringExtra(EXTRA_RINGTONE_URI))
            putExtra(EXTRA_LABEL, intent.getStringExtra(EXTRA_LABEL))
            putExtra(EXTRA_SNOOZE_ENABLED, intent.getBooleanExtra(EXTRA_SNOOZE_ENABLED, true))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_VIBRATE = "extra_vibrate"
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_SNOOZE_ENABLED = "extra_snooze_enabled"
    }
}
