package com.sleepguardian.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarms",
    indices = [
        Index(value = ["isEnabled"]),
        Index(value = ["hour", "minute"])
    ]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: String,
    val isEnabled: Boolean = true,
    val taskType: String = "MATH",
    val taskDifficulty: String = "MEDIUM",
    val ringtoneUri: String? = null,
    val label: String? = null,
    val vibrate: Boolean = true,
    val snoozeEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
