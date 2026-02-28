# MVP 1.4: Task System — Math Challenge

**Итерация:** 1 (MVP)
**Срок:** 4-6 дней
**Статус:** Завершена

## Цель

Создать движок заданий и реализовать математические задачи. Будильник нельзя отключить без решения всех примеров.

## Задачи

- [x] Архитектура Task Engine:
  - Interface `Task` с методами `generate()`, `validate()`, `getCurrentProgress()`, `reset()`
  - `TaskData` — данные задания (вопрос, подсказка, метаданные)
  - `TaskProgress` — прогресс (current, total, mistakes)
  - `TaskFactory` — создание заданий по типу и сложности
- [x] Реализация `MathTask`:
  - Easy: однозначные числа, +/- (3 примера)
  - Medium: двузначные числа, +/-/* (4 примера)
  - Hard: смешанные операции, крупные числа (5 примеров)
  - Валидация ответа
  - Счётчик попыток и ошибок
- [x] UI для ActiveAlarmScreen — секция задания:
  - Показ текущего примера крупным шрифтом
  - OutlinedTextField для ввода ответа (NumericKeyboard)
  - Кнопка "Проверить"
  - Анимация: зелёная/красная вспышка фона при правильном/неправильном ответе
  - LinearProgressIndicator: "3/5 решено"
  - Кнопка "Выключить" показывается ТОЛЬКО после решения всех примеров
- [x] Настройка задания при создании будильника:
  - Выбор типа задания (уже было в AddEditAlarmScreen)
  - Выбор сложности (уже было в AddEditAlarmScreen)
  - taskType и difficulty передаются через AlarmScheduler → AlarmReceiver → ActiveAlarmActivity
- [x] Логирование: `AlarmLogEntity` + `AlarmLogDao` + миграция БД v1→v2

## Task Interface

```kotlin
interface Task {
    fun generate(): TaskData
    fun validate(answer: String): Boolean
    fun getCurrentProgress(): TaskProgress
    fun reset()
}

data class TaskData(
    val question: String,
    val hint: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class TaskProgress(
    val current: Int,
    val total: Int,
    val mistakes: Int
)
```

## Примеры заданий по сложности

| Сложность | Пример | Диапазон |
|-----------|--------|----------|
| Easy | 7 + 5 = ? | 1-9, только +/- |
| Medium | 34 * 7 = ? | 10-99, +/-/* |
| Hard | (47 * 3) + 28 = ? | 10-999, смешанные |

## Критерии завершения

- Математические задания генерируются без повторов
- Валидация ответов работает корректно
- Нельзя отключить будильник без решения всех примеров
- Progress bar отображает прогресс
- Feedback при правильном/неправильном ответе
- Настройка типа и сложности при создании будильника

## Зависимости

- MVP 1.3 (будильник срабатывает, ActiveAlarmActivity существует)

## Риски

- Генерация слишком сложных/простых примеров — тестировать диапазоны
- UX: ввод ответа сразу после пробуждения должен быть максимально удобным
