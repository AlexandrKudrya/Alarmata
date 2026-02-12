# MVP 1.2: Alarm CRUD + Local Storage

**Итерация:** 1 (MVP)
**Срок:** 5-7 дней
**Статус:** Завершена

## Цель

Реализовать полный CRUD для будильников с сохранением в локальную БД. Пользователь может создавать, редактировать, удалять и включать/выключать будильники.

## Задачи

- [x] Room Entity для будильника (`AlarmEntity`)
- [x] `AlarmDao` с CRUD-операциями
- [x] Domain model `Alarm` + маппер Entity <-> Domain (`AlarmMapper`)
- [x] `AlarmRepository` (interface) + `AlarmRepositoryImpl`
- [x] Use cases: `CreateAlarmUseCase`, `UpdateAlarmUseCase`, `DeleteAlarmUseCase`, `GetAlarmsUseCase`, `GetAlarmByIdUseCase`, `ToggleAlarmUseCase`
- [x] `AlarmListViewModel` — загрузка, toggle, удаление (sealed `AlarmListUiState`)
- [x] `AddEditAlarmViewModel` — создание и редактирование (с `SavedStateHandle`, `SharedFlow` событиями)
- [x] UI: AlarmListScreen
  - Список будильников (LazyColumn)
  - Карточка будильника: время, дни недели, toggle (`AlarmCard`)
  - FAB для добавления
  - SwipeToDismissBox для удаления
  - Empty state, loading state, error state
- [x] UI: AddEditAlarmScreen
  - TimePicker (24h системный)
  - Выбор дней недели (FilterChips — `DayOfWeekSelector`)
  - Label (опционально)
  - Выбор типа задания (FilterChips: Math, QR Code, Shake, Memory, Typing, AI Question)
  - Выбор сложности (FilterChips: Easy, Medium, Hard)
  - Toggle вибрации
  - Toggle Snooze
  - Кнопка сохранения (Check icon в TopAppBar)
- [x] Удаление с диалогом подтверждения (`DeleteAlarmDialog`)
- [x] Навигация edit_alarm/{alarmId} для редактирования
- [x] Hilt DI: `RepositoryModule` для привязки `AlarmRepositoryImpl` к `AlarmRepository`
- [x] CI/CD: GitHub Actions (android-build.yml, quick-apk.yml)

## Database Schema

```kotlin
@Entity(
    tableName = "alarms",
    indices = [Index("isEnabled"), Index("hour", "minute")]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: String,      // "MONDAY,WEDNESDAY,FRIDAY"
    val isEnabled: Boolean,
    val taskType: String,         // "MATH"
    val taskDifficulty: String,   // "MEDIUM"
    val ringtoneUri: String?,
    val label: String?,
    val vibrate: Boolean,
    val snoozeEnabled: Boolean,
    val createdAt: Long
)
```

## Критерии завершения

- Создание будильника сохраняет в БД
- Список будильников отображается корректно
- Редактирование обновляет запись
- Удаление с подтверждением работает
- Toggle включает/выключает будильник
- Данные сохраняются между запусками приложения

## Зависимости

- MVP 1.1 (проект и база создана)

## Риски

- Сериализация `Set<DayOfWeek>` в Room — решено через TypeConverter + AlarmMapper
- Доступ к системным рингтонам — `RingtoneManager` (будет реализовано в следующих итерациях)
