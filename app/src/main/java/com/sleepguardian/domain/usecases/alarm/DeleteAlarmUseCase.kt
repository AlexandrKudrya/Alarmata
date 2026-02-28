package com.sleepguardian.domain.usecases.alarm

import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.repositories.AlarmRepository
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm) = repository.deleteAlarm(alarm)
}
