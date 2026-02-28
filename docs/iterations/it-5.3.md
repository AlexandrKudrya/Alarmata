# Iteration 5.3: Export, Profiles, Anti-Sabotage

**Итерация:** 5 (Gamification + Advanced Features)
**Срок:** 5-7 дней
**Статус:** Не начата

## Цель

Финальные фичи: экспорт данных, профили будильников, защита от саботажа, виджет.

## Задачи

### Export & Backup
- [ ] CSV/JSON export всех данных (будильники, логи, сессии сна)
- [ ] Выбор диапазона дат
- [ ] Share через Intent (email, мессенджеры, облако)
- [ ] Google Drive backup (опционально)
- [ ] Restore из backup

### Alarm Profiles
- [ ] `AlarmProfileEntity` — профили настроек
- [ ] Предустановленные: "Будни", "Выходные", "Важный день"
- [ ] Кастомные профили
- [ ] Быстрое переключение одной кнопкой
- [ ] Автоприменение по дням недели

### Anti-Sabotage
- [ ] Lock Mode в Settings:
  - Пароль/PIN для отключения будильника в настройках
  - Нельзя удалить будильник без пароля
  - Нельзя уменьшить громкость
- [ ] Логирование "подозрительных" действий:
  - Попытки отключить будильник
  - Быстрый snooze несколько раз подряд
  - Удаление задания перед пробуждением
- [ ] Confirmation dialogs для деструктивных действий

### Home Screen Widget
- [ ] Следующий будильник: время и обратный отсчёт
- [ ] Quick toggle вкл/выкл
- [ ] Кнопка быстрого доступа к Sleep Calculator
- [ ] Glance API (Jetpack) для виджета

### "Why I do this"
- [ ] Экран с мотивацией: зачем я встаю рано
- [ ] Пользователь пишет свои цели
- [ ] Показ при длительном snooze или провале задания

## Profile Schema

```kotlin
@Entity(tableName = "alarm_profiles")
data class AlarmProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val taskType: String,
    val difficulty: String,
    val ringtoneUri: String?,
    val vibrate: Boolean,
    val smartWindow: Boolean,
    val snoozeEnabled: Boolean,
    val daysOfWeek: String?
)
```

## Критерии завершения

- Export/Import работает
- Профили быстро переключаются
- Anti-sabotage предотвращает обходы
- Widget отображает актуальную информацию
- Приложение полнофункционально

## Зависимости

- Все предыдущие итерации
