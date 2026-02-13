package com.sleepguardian.domain.models

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val isEnabled: Boolean = true,
    val taskType: TaskType = TaskType.MATH,
    val taskDifficulty: Difficulty = Difficulty.MEDIUM,
    val ringtoneUri: String? = null,
    val label: String? = null,
    val vibrate: Boolean = true,
    val snoozeEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val timeFormatted: String
        get() = "%02d:%02d".format(hour, minute)

    val daysFormatted: String
        get() = when {
            daysOfWeek.isEmpty() -> "Once"
            daysOfWeek.size == 7 -> "Every day"
            daysOfWeek == WEEKDAYS -> "Weekdays"
            daysOfWeek == WEEKENDS -> "Weekends"
            else -> daysOfWeek
                .sortedBy { it.ordinal }
                .joinToString(", ") { it.shortName }
        }

    companion object {
        val WEEKDAYS = setOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        )
        val WEEKENDS = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }
}

enum class DayOfWeek(val shortName: String, val fullName: String) {
    MONDAY("Mon", "Monday"),
    TUESDAY("Tue", "Tuesday"),
    WEDNESDAY("Wed", "Wednesday"),
    THURSDAY("Thu", "Thursday"),
    FRIDAY("Fri", "Friday"),
    SATURDAY("Sat", "Saturday"),
    SUNDAY("Sun", "Sunday");
}

enum class TaskType(val displayName: String) {
    MATH("Math"),
    QR_CODE("QR Code"),
    SHAKE("Shake"),
    MEMORY("Memory"),
    TYPING("Typing"),
    AI_QUESTION("AI Question");
}

enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");
}
