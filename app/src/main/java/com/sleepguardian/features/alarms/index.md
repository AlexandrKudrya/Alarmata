# alarms

Alarm list and CRUD feature.

## Subfolders

- `ui/` — Composable screens: AlarmListScreen (with SwipeToDismiss, empty/loading/error states), AddEditAlarmScreen (TimePicker, day selector, task type/difficulty, toggles). Contains `components/` subfolder with AlarmCard, DayOfWeekSelector, DeleteAlarmDialog.
- `viewmodel/` — AlarmListViewModel (list loading, toggle, delete with sealed UI state), AddEditAlarmViewModel (create/edit state management with SavedStateHandle, SharedFlow events).
