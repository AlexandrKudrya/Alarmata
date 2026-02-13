package com.sleepguardian.features.alarms.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleepguardian.domain.models.Alarm
import com.sleepguardian.domain.models.DayOfWeek
import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.domain.models.TaskType
import com.sleepguardian.domain.usecases.alarm.CreateAlarmUseCase
import com.sleepguardian.domain.usecases.alarm.GetAlarmByIdUseCase
import com.sleepguardian.domain.usecases.alarm.UpdateAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAlarmViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val createAlarmUseCase: CreateAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase
) : ViewModel() {

    private val alarmId: Long = savedStateHandle["alarmId"] ?: -1L
    val isEditMode: Boolean = alarmId != -1L

    private val _state = MutableStateFlow(AddEditAlarmState())
    val state: StateFlow<AddEditAlarmState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddEditAlarmEvent>()
    val events: SharedFlow<AddEditAlarmEvent> = _events.asSharedFlow()

    init {
        if (isEditMode) {
            loadAlarm()
        }
    }

    private fun loadAlarm() {
        viewModelScope.launch {
            val alarm = getAlarmByIdUseCase(alarmId) ?: return@launch
            _state.value = AddEditAlarmState(
                hour = alarm.hour,
                minute = alarm.minute,
                daysOfWeek = alarm.daysOfWeek,
                label = alarm.label ?: "",
                vibrate = alarm.vibrate,
                ringtoneUri = alarm.ringtoneUri,
                taskType = alarm.taskType,
                taskDifficulty = alarm.taskDifficulty,
                snoozeEnabled = alarm.snoozeEnabled
            )
        }
    }

    fun setTime(hour: Int, minute: Int) {
        _state.update { it.copy(hour = hour, minute = minute) }
    }

    fun toggleDay(day: DayOfWeek) {
        _state.update { current ->
            val newDays = current.daysOfWeek.toMutableSet()
            if (day in newDays) newDays.remove(day) else newDays.add(day)
            current.copy(daysOfWeek = newDays)
        }
    }

    fun setLabel(label: String) {
        _state.update { it.copy(label = label) }
    }

    fun setVibrate(vibrate: Boolean) {
        _state.update { it.copy(vibrate = vibrate) }
    }

    fun setRingtoneUri(uri: String?) {
        _state.update { it.copy(ringtoneUri = uri) }
    }

    fun setTaskType(type: TaskType) {
        _state.update { it.copy(taskType = type) }
    }

    fun setTaskDifficulty(difficulty: Difficulty) {
        _state.update { it.copy(taskDifficulty = difficulty) }
    }

    fun setSnoozeEnabled(enabled: Boolean) {
        _state.update { it.copy(snoozeEnabled = enabled) }
    }

    fun save() {
        viewModelScope.launch {
            val s = _state.value
            val alarm = Alarm(
                id = if (isEditMode) alarmId else 0,
                hour = s.hour,
                minute = s.minute,
                daysOfWeek = s.daysOfWeek,
                label = s.label.ifBlank { null },
                vibrate = s.vibrate,
                ringtoneUri = s.ringtoneUri,
                taskType = s.taskType,
                taskDifficulty = s.taskDifficulty,
                snoozeEnabled = s.snoozeEnabled
            )
            if (isEditMode) {
                updateAlarmUseCase(alarm)
            } else {
                createAlarmUseCase(alarm)
            }
            _events.emit(AddEditAlarmEvent.Saved)
        }
    }
}

data class AddEditAlarmState(
    val hour: Int = 7,
    val minute: Int = 0,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val label: String = "",
    val vibrate: Boolean = true,
    val ringtoneUri: String? = null,
    val taskType: TaskType = TaskType.MATH,
    val taskDifficulty: Difficulty = Difficulty.MEDIUM,
    val snoozeEnabled: Boolean = true
)

sealed interface AddEditAlarmEvent {
    data object Saved : AddEditAlarmEvent
}
