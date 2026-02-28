package com.sleepguardian.data.repository

import com.sleepguardian.data.local.entities.AlarmEntity
import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.models.DayOfWeek
import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.domain.models.TaskType

object AlarmMapper {

    fun toDomain(entity: AlarmEntity): Alarm = Alarm(
        id = entity.id,
        hour = entity.hour,
        minute = entity.minute,
        daysOfWeek = parseDaysOfWeek(entity.daysOfWeek),
        isEnabled = entity.isEnabled,
        taskType = TaskType.valueOf(entity.taskType),
        taskDifficulty = Difficulty.valueOf(entity.taskDifficulty),
        ringtoneUri = entity.ringtoneUri,
        label = entity.label,
        vibrate = entity.vibrate,
        snoozeEnabled = entity.snoozeEnabled,
        createdAt = entity.createdAt
    )

    fun toEntity(alarm: Alarm): AlarmEntity = AlarmEntity(
        id = alarm.id,
        hour = alarm.hour,
        minute = alarm.minute,
        daysOfWeek = alarm.daysOfWeek.joinToString(",") { it.name },
        isEnabled = alarm.isEnabled,
        taskType = alarm.taskType.name,
        taskDifficulty = alarm.taskDifficulty.name,
        ringtoneUri = alarm.ringtoneUri,
        label = alarm.label,
        vibrate = alarm.vibrate,
        snoozeEnabled = alarm.snoozeEnabled,
        createdAt = alarm.createdAt
    )

    private fun parseDaysOfWeek(value: String): Set<DayOfWeek> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .filter { it.isNotBlank() }
            .map { DayOfWeek.valueOf(it.trim()) }
            .toSet()
    }
}
