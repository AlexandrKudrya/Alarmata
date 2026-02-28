package com.sleepguardian.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sleepguardian.core.alarm.AlarmReceiver
import com.sleepguardian.core.alarm.AlarmService

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        when (intent.action) {
            NotificationHelper.ACTION_DISMISS -> {
                val stopIntent = Intent(context, AlarmService::class.java).apply {
                    action = AlarmService.ACTION_STOP
                    putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
                }
                context.startService(stopIntent)
            }
            NotificationHelper.ACTION_SNOOZE -> {
                val snoozeIntent = Intent(context, AlarmService::class.java).apply {
                    action = AlarmService.ACTION_SNOOZE
                    putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
                }
                context.startService(snoozeIntent)
            }
        }
    }
}
