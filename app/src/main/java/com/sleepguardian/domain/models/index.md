# domain.models

Domain model classes representing core business entities.

## Files
- `Alarm.kt` — Alarm domain model with time, repeat pattern, tasks, and settings. Includes `DayOfWeek`, `TaskType`, `Difficulty` enums and computed `timeFormatted`/`daysFormatted` properties.
- `AlarmLog.kt` — *(planned)* Log entry recording alarm dismissal events and completion data.
- `Task.kt` — *(planned)* Task model representing wake-up tasks (type, difficulty, configuration).
- `Achievement.kt` — *(planned)* Achievement/badge model for user accomplishments.
- `SleepSession.kt` — *(planned)* Sleep session model tracking bedtime, wake time, and quality.
- `BedtimeChecklist.kt` — *(planned)* Bedtime routine checklist item model.
