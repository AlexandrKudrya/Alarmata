# data.repository

Repository interface implementations.

## Files
- `AlarmRepositoryImpl.kt` — Implementation of AlarmRepository using AlarmDao + AlarmMapper for Room persistence.
- `AlarmMapper.kt` — Stateless mapper object converting AlarmEntity <-> Alarm domain model. Handles DayOfWeek serialization.
- `StatisticsRepositoryImpl.kt` — *(planned)* Implementation of StatisticsRepository aggregating sleep data and analytics.
- `TaskRepositoryImpl.kt` — *(planned)* Implementation of TaskRepository handling task generation (local and AI-based).
- `BedtimeRepositoryImpl.kt` — *(planned)* Implementation of BedtimeRepository managing bedtime routines and checklists.
