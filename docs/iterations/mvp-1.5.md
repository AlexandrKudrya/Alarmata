# MVP 1.5: Additional Tasks + Statistics

**Итерация:** 1 (MVP)
**Срок:** 5-7 дней
**Статус:** Не начата

## Цель

Добавить QR-код и shake-задания. Реализовать базовую статистику пробуждений. После этой итерации MVP полнофункционален.

## Задачи

### QR Code Scanner Task
- [ ] Интеграция CameraX + ML Kit Barcode Scanning
- [ ] UI для сохранения QR-кода при настройке будильника:
  - Кнопка "Отсканировать QR" в AddEditAlarmScreen
  - Превью камеры → сканирование → сохранение значения
- [ ] UI при пробуждении:
  - Камера открывается
  - Пользователь сканирует нужный QR
  - Сравнение с сохранённым значением
- [ ] Permission handling для камеры (`CAMERA`)
- [ ] Fallback если камера недоступна → переключение на Math

### Shake Phone Task
- [ ] Sensor API (TYPE_ACCELEROMETER)
- [ ] Алгоритм детекции встряхивания:
  - Threshold: acceleration > 12 m/s²
  - Debounce: 300ms между встряхиваниями
  - Счётчик: Easy=10, Medium=20, Hard=30 встряхиваний
- [ ] UI:
  - Иконка телефона с анимацией тряски
  - Счётчик: "15/20 встряхиваний"
  - Прогресс-бар с анимацией

### Настройка задания
- [ ] Обновить AddEditAlarmScreen:
  - Dropdown/Chips для выбора типа задания: Math / QR Code / Shake
  - Dropdown для сложности
  - Если QR — показать кнопку сканирования

### Базовая статистика
- [ ] `AlarmLogEntity` + `AlarmLogDao`
- [ ] Логирование при каждом срабатывании:
  - Время срабатывания
  - Было ли завершено
  - Количество попыток и ошибок
  - Тип задания
  - Количество snooze
- [ ] Простой экран статистики:
  - "Встал вовремя X из Y раз" (процент)
  - Последние 7 дней — список
  - Среднее время решения задания

## AlarmLog Schema

```kotlin
@Entity(
    tableName = "alarm_logs",
    foreignKeys = [ForeignKey(
        entity = AlarmEntity::class,
        parentColumns = ["id"],
        childColumns = ["alarmId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AlarmLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alarmId: Long,
    val triggeredAt: Long,
    val completedAt: Long?,
    val wasCompleted: Boolean,
    val attempts: Int,
    val mistakes: Int,
    val taskType: String,
    val snoozedCount: Int
)
```

## Критерии завершения

- 3 типа заданий работают: Math, QR Code, Shake
- QR-код: сканирование при настройке и при пробуждении
- Shake: детекция встряхивания надёжная
- Выбор типа задания при создании будильника
- Статистика отображает процент успешных пробуждений
- **MVP полностью функционален — можно использовать как основной будильник**

## Зависимости

- MVP 1.4 (Task Engine, ActiveAlarmScreen с заданиями)

## Риски

- ML Kit может быть тяжёлым — использовать bundled model
- Акселерометр ведёт себя по-разному на разных устройствах — настраиваемый threshold
- Камера может не быть доступна → обязательный fallback
