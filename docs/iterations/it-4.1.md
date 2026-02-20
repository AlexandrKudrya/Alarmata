# Iteration 4.1: Bedtime Mode + Checklist

**Итерация:** 4 (Вечерний режим + Ритуалы)
**Срок:** 4-5 дней
**Статус:** Не начата

## Цель

Вечерний режим с настраиваемым чеклистом подготовки ко сну. Помогает выработать рутину.

## Задачи

- [ ] `BedtimeModeScreen` — основной экран вечернего режима
- [ ] Вечерний чеклист:
  - Дефолтные пункты: "Почистить зубы", "Поставить зарядку", "Закрыть шторы", "Стакан воды"
  - CRUD для кастомных пунктов
  - Checkbox UI с анимацией (strikethrough при завершении)
  - Progress bar: "3/5 выполнено"
  - Сохранение состояния
- [ ] Разные чеклисты для будней и выходных
- [ ] Напоминание начать режим:
  - Notification за 30 мин до bedtime (настраивается)
  - Deeplink в BedtimeModeScreen
- [ ] `BedtimeChecklistEntity` + `BedtimeLogEntity`
- [ ] История выполнения чеклиста
- [ ] Добавить в навигацию (вкладка или кнопка)

## Database Schema

```kotlin
@Entity(tableName = "bedtime_checklists")
data class BedtimeChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val items: String,        // JSON: ["Зубы", "Зарядка", ...]
    val isWeekday: Boolean
)

@Entity(tableName = "bedtime_logs")
data class BedtimeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val checklistId: Long,
    val completedItems: String,  // JSON
    val wasFullyCompleted: Boolean
)
```

## Критерии завершения

- Чеклист отображается и сохраняет состояние
- Кастомные пункты добавляются и удаляются
- Разные чеклисты для будней/выходных
- Напоминание приходит вовремя
- История выполнения записывается

## Зависимости

- MVP 1.1 (навигация, базовый UI)
