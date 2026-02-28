package com.sleepguardian.data.repository

import com.sleepguardian.data.local.dao.AlarmDao
import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.repositories.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> =
        alarmDao.getAllAlarms().map { entities ->
            entities.map(AlarmMapper::toDomain)
        }

    override suspend fun getAlarmById(id: Long): Alarm? =
        alarmDao.getAlarmById(id)?.let(AlarmMapper::toDomain)

    override suspend fun insertAlarm(alarm: Alarm): Long =
        alarmDao.insert(AlarmMapper.toEntity(alarm))

    override suspend fun updateAlarm(alarm: Alarm) =
        alarmDao.update(AlarmMapper.toEntity(alarm))

    override suspend fun deleteAlarm(alarm: Alarm) =
        alarmDao.delete(AlarmMapper.toEntity(alarm))

    override suspend fun setEnabled(id: Long, isEnabled: Boolean) =
        alarmDao.setEnabled(id, isEnabled)
}
