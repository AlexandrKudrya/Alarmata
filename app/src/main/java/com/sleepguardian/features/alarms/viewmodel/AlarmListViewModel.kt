package com.sleepguardian.features.alarms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.usecases.alarm.DeleteAlarmUseCase
import com.sleepguardian.domain.usecases.alarm.GetAlarmsUseCase
import com.sleepguardian.domain.usecases.alarm.ToggleAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlarmListUiState>(AlarmListUiState.Loading)
    val uiState: StateFlow<AlarmListUiState> = _uiState.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            getAlarmsUseCase()
                .catch { e ->
                    _uiState.value = AlarmListUiState.Error(e.message ?: "Unknown error")
                }
                .collect { alarms ->
                    _uiState.value = AlarmListUiState.Success(alarms)
                }
        }
    }

    fun toggleAlarm(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            toggleAlarmUseCase(id, isEnabled)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            deleteAlarmUseCase(alarm)
        }
    }
}

sealed interface AlarmListUiState {
    data object Loading : AlarmListUiState
    data class Success(val alarms: List<Alarm>) : AlarmListUiState
    data class Error(val message: String) : AlarmListUiState
}
