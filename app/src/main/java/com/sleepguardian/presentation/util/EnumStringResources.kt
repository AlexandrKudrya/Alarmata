package com.sleepguardian.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sleepguardian.R
import com.sleepguardian.domain.models.DayOfWeek
import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.domain.models.TaskType

@get:StringRes
val DayOfWeek.shortNameRes: Int
    get() = when (this) {
        DayOfWeek.MONDAY -> R.string.day_mon
        DayOfWeek.TUESDAY -> R.string.day_tue
        DayOfWeek.WEDNESDAY -> R.string.day_wed
        DayOfWeek.THURSDAY -> R.string.day_thu
        DayOfWeek.FRIDAY -> R.string.day_fri
        DayOfWeek.SATURDAY -> R.string.day_sat
        DayOfWeek.SUNDAY -> R.string.day_sun
    }

@get:StringRes
val TaskType.displayNameRes: Int
    get() = when (this) {
        TaskType.MATH -> R.string.task_math
        TaskType.QR_CODE -> R.string.task_qr_code
        TaskType.SHAKE -> R.string.task_shake
        TaskType.MEMORY -> R.string.task_memory
        TaskType.TYPING -> R.string.task_typing
        TaskType.AI_QUESTION -> R.string.task_ai_question
    }

@get:StringRes
val Difficulty.displayNameRes: Int
    get() = when (this) {
        Difficulty.EASY -> R.string.difficulty_easy
        Difficulty.MEDIUM -> R.string.difficulty_medium
        Difficulty.HARD -> R.string.difficulty_hard
    }

@Composable
fun formatDaysOfWeek(days: Set<DayOfWeek>): String {
    val weekdays = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )
    val weekends = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    return when {
        days.isEmpty() -> stringResource(R.string.days_once)
        days.size == 7 -> stringResource(R.string.days_every_day)
        days == weekdays -> stringResource(R.string.days_weekdays)
        days == weekends -> stringResource(R.string.days_weekends)
        else -> {
            val sorted = days.sortedBy { it.ordinal }
            val names = sorted.map { stringResource(it.shortNameRes) }
            names.joinToString(", ")
        }
    }
}
