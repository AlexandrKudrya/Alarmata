# MVP 1.2: Alarm CRUD + Local Storage

**Итерация:** 1 (MVP)
**Срок:** 5-7 дней
**Статус:** Не начата

## Цель

Реализовать полный CRUD для будильников с сохранением в локальную БД. Пользователь может создавать, редактировать, удалять и включать/выключать будильники.

## Задачи

- [ ] Room Entity для будильника (`AlarmEntity`)
- [ ] `AlarmDao` с CRUD-операциями
- [ ] Domain model `Alarm` + маппер Entity <-> Domain
- [ ] `AlarmRepository` (interface) + `AlarmRepositoryImpl`
- [ ] Use cases: `CreateAlarmUseCase`, `UpdateAlarmUseCase`, `DeleteAlarmUseCase`, `GetAlarmsUseCase`
- [ ] `AlarmListViewModel` — загрузка, toggle, удаление
- [ ] `AddEditAlarmViewModel` — создание и редактирование
- [ ] UI: AlarmListScreen
  - Список будильников (LazyColumn)
  - Карточка будильника: время, дни недели, toggle
  - FAB для добавления
  - Свайп или долгое нажатие для удаления
- [ ] UI: AddEditAlarmScreen
  - TimePicker (системный)
  - Выбор дней недели (чипы)
  - Выбор мелодии (из системных рингтонов)
  - Label (опционально)
  - Toggle вибрации
  - Кнопка "Сохранить"
- [ ] Удаление с диалогом подтверждения
- [ ] Сортировка по времени

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

- Сериализация `Set<DayOfWeek>` в Room — решать через TypeConverter
- Доступ к системным рингтонам — `RingtoneManager`
