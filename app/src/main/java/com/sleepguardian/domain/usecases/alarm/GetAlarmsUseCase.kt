package com.sleepguardian.domain.usecases.alarm

import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.repositories.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> = repository.getAllAlarms()
}
