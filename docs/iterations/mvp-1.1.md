# MVP 1.1: Project Setup + Basic UI

**Итерация:** 1 (MVP)
**Срок:** 3-5 дней
**Статус:** Завершена

## Цель

Создать работающий Android-проект с базовой навигацией и UI-каркасом. После этой итерации приложение запускается, показывает экраны и позволяет навигироваться между ними.

## Задачи

- [x] Создать Android-проект с Kotlin + Jetpack Compose
- [x] Настроить Hilt для Dependency Injection
- [x] Настроить Room Database (базовая схема)
- [x] Создать основные экраны с навигацией:
  - Main screen со списком будильников (пока пустой)
  - Add/Edit Alarm screen (заглушка)
  - Settings screen (заглушка)
- [x] Базовый UI kit: Material 3 компоненты, кнопки, карточки
- [x] Bottom Navigation (Alarms, Statistics, Settings)
- [x] Настроить тему (цвета, типографика)

## Технические детали

### Зависимости
- Compose BOM 2023.10.01
- Material 3
- Hilt 2.48
- Room 2.6.0
- Navigation Compose 2.7.5

### Что создаётся
- `SleepGuardianApp.kt` — Application class с `@HiltAndroidApp`
- `MainActivity.kt` — Single Activity с Compose
- `NavGraph.kt` — навигационный граф
- `Theme.kt`, `Color.kt`, `Typography.kt` — тема приложения
- `AppDatabase.kt` — Room database (пустая)
- Hilt-модули: `DatabaseModule`, `AppModule`

### Структура пакетов
```
com.sleepguardian/
├── app/
├── core/di/
├── core/database/
├── features/alarms/ui/
├── features/settings/ui/
└── presentation/theme/
```

## Критерии завершения

- Приложение запускается без крашей
- Навигация между экранами работает
- Тема Material 3 применяется
- Hilt инжектит зависимости
- Room database создаётся при первом запуске

## Зависимости

Нет — это первая итерация.

## Риски

- Конфликты версий Compose/Kotlin/Hilt — решать через BOM
- KSP vs KAPT для Room/Hilt — использовать KSP
