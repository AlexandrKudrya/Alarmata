# entities

Room database entities (data layer).

## Files

- `AlarmEntity.kt` — Alarm table. Fields: id, hour, minute, daysOfWeek, isEnabled, taskType, taskDifficulty, ringtoneUri, label, vibrate, snoozeEnabled, createdAt. Indexed on isEnabled and (hour, minute).
