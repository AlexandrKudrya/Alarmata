# MVP 1.3: Alarm Triggering + Wake Lock

**Итерация:** 1 (MVP)
**Срок:** 5-7 дней
**Статус:** Завершена

## Цель

Будильник надёжно срабатывает в назначенное время, включая экран, воспроизводя звук и вибрацию. Экран показывается поверх lock screen. Временная кнопка отключения (без задания).

## Задачи

- [x] `AlarmScheduler` — обёртка над AlarmManager
  - `schedule(alarm)` — планирование
  - `cancel(alarmId)` — отмена
  - `rescheduleAll()` — пересоздание всех после ребута (через `BootReceiver`)
  - Расчёт следующего срабатывания с учётом дней недели
- [x] `AlarmReceiver` (BroadcastReceiver)
  - Получает Intent от AlarmManager
  - Запускает AlarmService + ActiveAlarmActivity напрямую
- [x] `BootReceiver` — восстановление будильников после перезагрузки
- [x] `AlarmService` (Foreground Service)
  - Запуск вибрации (VibrationEffect pattern)
  - Воспроизведение рингтона через MediaPlayer
  - Нарастающая громкость (0% -> 100% за 10 сек)
  - Foreground notification
  - Auto-stop через 5 минут
- [x] `ActiveAlarmActivity` — полноэкранная Activity
  - Флаги: `showWhenLocked`, `turnScreenOn`
  - Блокировка back button
  - Отображение времени, label
  - Кнопка "Отключить" (временная, без задания)
  - Кнопка "Snooze" (5 минут)
- [x] Интеграция: при создании/изменении будильника вызывать AlarmScheduler
- [x] Обработка пермишенов: `SCHEDULE_EXACT_ALARM`, `POST_NOTIFICATIONS`, `USE_FULL_SCREEN_INTENT`

## Критические разрешения

```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## Архитектурные заметки

- `AlarmManager.setExactAndAllowWhileIdle()` для надёжности в Doze mode
- `PendingIntent.FLAG_IMMUTABLE` обязателен для Android 12+
- WorkManager как backup: если AlarmManager не сработал, WorkManager проверяет и триггерит
- WakeLock с таймаутом 10 минут — safety net

## Критерии завершения

- Будильник срабатывает точно в назначенное время
- Экран включается и показывается поверх lock screen
- Звук с нарастающей громкостью играет
- Вибрация работает
- Snooze откладывает на 5 минут
- После перезагрузки телефона будильники восстанавливаются
- Кнопка "Отключить" останавливает звук и вибрацию

## Зависимости

- MVP 1.2 (будильники сохраняются в БД)

## Риски

- Battery optimization убивает AlarmManager — запрос exemption
- Android 14 ужесточил `SCHEDULE_EXACT_ALARM` — нужен fallback на `canScheduleExactAlarms()`
- Различия поведения на Samsung/Xiaomi/Huawei — тестирование на реальных устройствах
