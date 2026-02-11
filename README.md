# Sleep Guardian (Alarmata)

Android-приложение для умного пробуждения. Нельзя отключить будильник без выполнения задания.

## Tech Stack

- Kotlin 1.9+ / Jetpack Compose / Material 3
- Clean Architecture + MVVM
- Hilt (DI) / Room (DB) / Coroutines + Flow
- CameraX + ML Kit (QR) / Sensors API (Shake)
- Retrofit (Claude API)

## Документация

Вся документация в папке [`docs/`](docs/index.md):

- [ROADMAP.md](docs/ROADMAP.md) — дорожная карта (5 итераций, 17 подитераций)
- [ARCHITECTURE.md](docs/ARCHITECTURE.md) — техническая архитектура
- [CI-CD.md](docs/CI-CD.md) — CI/CD pipeline
- [CODESTYLE.md](docs/CODESTYLE.md) — code style guide
- [RULES.md](docs/RULES.md) — правила для AI-разработчика
- [MANUAL-TASKS.md](docs/MANUAL-TASKS.md) — задачи, требующие участия человека
- [iterations/](docs/iterations/index.md) — описание каждой подитерации

## Структура проекта

```
app/src/main/java/com/sleepguardian/
├── app/            # Application, MainActivity
├── core/           # DI, Database, AlarmScheduler, Utils
├── domain/         # Models, Repository interfaces, Use Cases
├── data/           # Room entities/DAOs, API, Repository implementations
├── features/       # UI + ViewModels по фичам
├── presentation/   # Тема, навигация
└── widget/         # Home screen widget
```

## Лицензия

Apache 2.0
