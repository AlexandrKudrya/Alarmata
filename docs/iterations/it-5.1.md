# Iteration 5.1: Achievements + Streaks

**Итерация:** 5 (Gamification + Advanced Features)
**Срок:** 4-5 дней
**Статус:** Не начата

## Цель

Система достижений и стриков для долгосрочной мотивации. Gamification без токсичности.

## Задачи

### Achievement System
- [ ] `AchievementEntity` — Room entity
- [ ] Список достижений:
  - "Первое пробуждение" — первый отключённый будильник
  - "Неделя стабильности" — 7 дней подряд
  - "Месяц дисциплины" — 30 дней подряд
  - "Сотня" — 100 пробуждений
  - "Мастер математики" — 50 мат. заданий без ошибок
  - "Ранняя пташка" — 5 дней подряд до 7:00
  - "Безупречный" — 10 заданий подряд без ошибок
  - "Сканер" — 20 QR-кодов отсканировано
  - "Трясун" — 500 shake суммарно
- [ ] `AchievementChecker` — проверка условий после каждого пробуждения
- [ ] Notification при разблокировке
- [ ] UI: экран достижений с бейджами (locked/unlocked)

### Streak Calendar
- [ ] GitHub-style heatmap (зелёные квадраты)
- [ ] Текущий streak (дни подряд)
- [ ] Лучший рекорд
- [ ] Мягкое отношение к срывам: "Потерял стрик, но у тебя 95% за месяц!"
- [ ] Custom Canvas drawing для heatmap

### Progress Screen
- [ ] Общая статистика: всего пробуждений, процент успеха
- [ ] Список достижений с прогрессом
- [ ] Streak calendar
- [ ] Добавить в навигацию

## Achievement Schema

```kotlin
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,     // "first_wake", "streak_7"
    val title: String,
    val description: String,
    val iconResName: String,         // resource name
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val target: Int = 1
)
```

## Критерии завершения

- 10+ достижений разблокируются корректно
- Heatmap calendar работает и выглядит красиво
- Streak считается правильно
- Notification при получении достижения

## Зависимости

- IT-3.3 (детальная статистика для подсчёта)
- MVP 1.5 (AlarmLog для анализа)
