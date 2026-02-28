package com.sleepguardian.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sleepguardian.data.local.entities.AlarmLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmLogDao {

    @Insert
    suspend fun insert(log: AlarmLogEntity): Long

    @Update
    suspend fun update(log: AlarmLogEntity)

    @Query("SELECT * FROM alarm_logs WHERE alarmId = :alarmId ORDER BY triggeredAt DESC")
    fun getLogsForAlarm(alarmId: Long): Flow<List<AlarmLogEntity>>

    @Query("SELECT * FROM alarm_logs ORDER BY triggeredAt DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 50): Flow<List<AlarmLogEntity>>
}
