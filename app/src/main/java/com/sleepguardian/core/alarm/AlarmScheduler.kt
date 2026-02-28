package com.sleepguardian.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.models.DayOfWeek
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        if (!alarm.isEnabled) {
            cancel(alarm.id)
            return
        }

        val triggerTime = getNextTriggerTime(alarm)
        val pendingIntent = createPendingIntent(alarm)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                pendingIntent
            )
        }
    }

    fun cancel(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_VIBRATE, alarm.vibrate)
            putExtra(AlarmReceiver.EXTRA_RINGTONE_URI, alarm.ringtoneUri)
            putExtra(AlarmReceiver.EXTRA_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, alarm.snoozeEnabled)
            putExtra(AlarmReceiver.EXTRA_TASK_TYPE, alarm.taskType.name)
            putExtra(AlarmReceiver.EXTRA_TASK_DIFFICULTY, alarm.taskDifficulty.name)
        }
        return PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        fun getNextTriggerTime(alarm: Alarm): Long {
            val now = Calendar.getInstance()
            val trigger = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (alarm.daysOfWeek.isEmpty()) {
                // One-time alarm: if time already passed today, schedule for tomorrow
                if (trigger.before(now) || trigger == now) {
                    trigger.add(Calendar.DAY_OF_MONTH, 1)
                }
            } else {
                // Repeating alarm: find next matching day
                val todayCalendarDay = now.get(Calendar.DAY_OF_WEEK)
                val todayDow = calendarDayToDayOfWeek(todayCalendarDay) ?: DayOfWeek.MONDAY
                val orderedDays = DayOfWeek.entries

                // Check if today is valid and time hasn't passed
                if (todayDow in alarm.daysOfWeek && trigger.after(now)) {
                    return trigger.timeInMillis
                }

                // Find next valid day
                var daysAhead = 1
                for (i in 1..7) {
                    val nextDow = orderedDays[(todayDow.ordinal + i) % 7]
                    if (nextDow in alarm.daysOfWeek) {
                        daysAhead = i
                        break
                    }
                }
                trigger.add(Calendar.DAY_OF_MONTH, daysAhead)
            }

            return trigger.timeInMillis
        }

        private fun calendarDayToDayOfWeek(calendarDay: Int): DayOfWeek? {
            return when (calendarDay) {
                Calendar.MONDAY -> DayOfWeek.MONDAY
                Calendar.TUESDAY -> DayOfWeek.TUESDAY
                Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                Calendar.THURSDAY -> DayOfWeek.THURSDAY
                Calendar.FRIDAY -> DayOfWeek.FRIDAY
                Calendar.SATURDAY -> DayOfWeek.SATURDAY
                Calendar.SUNDAY -> DayOfWeek.SUNDAY
                else -> null
            }
        }
    }
}
