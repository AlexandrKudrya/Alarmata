package com.sleepguardian.domain.usecases.alarm

import com.sleepguardian.domain.repositories.AlarmRepository
import javax.inject.Inject

class ToggleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(id: Long, isEnabled: Boolean) =
        repository.setEnabled(id, isEnabled)
}
