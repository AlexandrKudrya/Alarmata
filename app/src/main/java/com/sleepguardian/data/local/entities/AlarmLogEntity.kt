package com.sleepguardian.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarm_logs",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["alarmId"]),
        Index(value = ["triggeredAt"])
    ]
)
data class AlarmLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long,
    val triggeredAt: Long = System.currentTimeMillis(),
    val dismissedAt: Long? = null,
    val taskType: String,
    val taskDifficulty: String,
    val totalProblems: Int,
    val solvedProblems: Int,
    val mistakes: Int,
    val snoozed: Boolean = false
)
