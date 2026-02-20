# domain.usecases.alarm

Alarm-related use cases.

## Files
- `CreateAlarmUseCase.kt` — Creates a new alarm and saves to repository. Returns new alarm ID.
- `DeleteAlarmUseCase.kt` — Deletes an alarm by domain model.
- `UpdateAlarmUseCase.kt` — Updates an existing alarm in repository.
- `GetAlarmsUseCase.kt` — Returns Flow<List<Alarm>> of all alarms.
- `GetAlarmByIdUseCase.kt` — Returns a single Alarm? by ID.
- `ToggleAlarmUseCase.kt` — Enables or disables an alarm by ID and boolean flag.
