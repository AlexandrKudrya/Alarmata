# Sleep Guardian - Development Roadmap

> Умный будильник с адаптивными заданиями и системой нормализации сна

## Обзор проекта

**Sleep Guardian** - это Android-приложение для здорового пробуждения и нормализации режима сна. Основная идея: нельзя отключить будильник без выполнения задания, плюс комплексная система для улучшения качества сна.

**Ключевые отличия от аналогов:**
- Продвинутые адаптивные задания без подписки
- Интеграция с Claude API для динамических вопросов
- Полноценная аналитика сна и циклов
- Вечерний режим с ритуалами
- Gamification без токсичности

---

## Timeline и Структура

**Общая длительность:** 2-3 месяца активной разработки

**Структура:**
- 5 основных итераций
- 17 под-итераций
- Каждая под-итерация: 3-7 дней работы

---

# MVP (Iteration 1): Базовый функциональный будильник

**Цель:** Создать работающий будильник, который невозможно просто отключить

**Длительность:** 3-4 недели

---

## MVP 1.1: Project Setup + Basic UI
**Срок:** 3-5 дней

### Задачи
- [x] Создать Android проект с Kotlin + Jetpack Compose
- [x] Настроить Hilt для Dependency Injection
- [x] Настроить Room Database (базовая схема)
- [x] Создать основные экраны с навигацией:
  - Main screen со списком будильников
  - Add/Edit Alarm screen
  - Settings screen (заглушка)
- [x] Базовый UI kit: Material 3 компоненты, кнопки, карточки
- [x] Bottom navigation

### Технические детали
```kotlin
// Dependencies
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
}
```

### Deliverables
- ✅ Запускающееся приложение
- ✅ Навигация между экранами работает
- ✅ Базовая UI структура готова

---

## MVP 1.2: Alarm CRUD + Local Storage
**Срок:** 5-7 дней

### Задачи
- [x] Создать Room entities и схему БД
- [x] Реализовать Repository pattern
- [x] ViewModels для всех экранов
- [x] UI для создания будильника:
  - TimePicker (системный или кастомный)
  - Выбор дней недели (checkboxes с красивым UI)
  - Toggle on/off
  - Выбор мелодии (из системных рингтонов)
- [x] Список будильников на главном экране
- [x] Редактирование существующих будильников
- [x] Удаление с подтверждением
- [x] Сортировка по времени

### Database Schema
```kotlin
@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: Set<DayOfWeek>, // Serialized as String
    val isEnabled: Boolean = true,
    val ringtoneUri: String? = null,
    val taskType: TaskType = TaskType.MATH,
    val taskDifficulty: Difficulty = Difficulty.MEDIUM,
    val label: String? = null,
    val vibrate: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum class TaskType {
    MATH, QR_CODE, SHAKE, MEMORY, TYPING, AI_QUESTION
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}
```

### Deliverables
- ✅ CRUD операции работают
- ✅ Данные сохраняются в БД
- ✅ UI интуитивный и функциональный

---

## MVP 1.3: Alarm Triggering + Wake Lock
**Срок:** 5-7 дней

### Задачи
- [x] AlarmManager integration
- [x] Scheduling будильников с учётом дней недели
- [x] BootReceiver для восстановления будильников после перезагрузки
- [x] AlarmReceiver → запуск Foreground Service
- [x] WakeLockService:
  - Acquire WakeLock
  - Вибрация (VibrationEffect)
  - Звук через MediaPlayer с нарастающей громкостью
- [x] Полноэкранная Activity:
  - Показ поверх lock screen
  - Флаги: `SHOW_WHEN_LOCKED`, `TURN_SCREEN_ON`, `DISMISS_KEYGUARD`
  - Нельзя закрыть свайпом
- [x] Временная кнопка "Отключить" (без задания)
- [x] Snooze на 5 минут

### Критические разрешения
```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### Deliverables
- ✅ Будильник срабатывает точно в указанное время
- ✅ Экран включается даже на lock screen
- ✅ Звук и вибрация работают
- ✅ Будильники восстанавливаются после перезагрузки

---

## MVP 1.4: Task System - Math Challenge
**Срок:** 4-6 дней

### Задачи
- [x] Архитектура Task Engine:
  - Interface `Task` с методами `generate()`, `validate()`, `getDifficulty()`
  - Factory для создания заданий
- [x] Реализация MathTask:
  - Генерация примеров (сложение, вычитание, умножение)
  - 3-5 примеров подряд (настраивается)
  - Валидация ответов
  - Счётчик попыток и ошибок
- [x] UI для выполнения задания:
  - Показ текущего примера
  - TextField для ввода ответа
  - Кнопка "Проверить"
  - Feedback: зелёная/красная анимация
  - Progress bar: "3/5 примеров решено"
- [x] Блокировка кнопки "Отключить" до выполнения всех заданий
- [x] Настройка типа задания при создании будильника

### Task Interface
```kotlin
interface Task {
    fun generate(): TaskData
    fun validate(answer: String): Boolean
    fun getDifficulty(): Difficulty
    fun getProgress(): TaskProgress
}

data class TaskData(
    val question: String,
    val type: TaskType,
    val metadata: Map<String, Any> = emptyMap()
)

data class TaskProgress(
    val current: Int,
    val total: Int,
    val mistakes: Int
)
```

### Deliverables
- ✅ Математические задания работают
- ✅ Нельзя отключить будильник без решения
- ✅ UI интуитивный и responsive

---

## MVP 1.5: Additional Tasks + Statistics
**Срок:** 5-7 дней

### Задачи
- [x] QR Code Scanner Task:
  - Интеграция CameraX
  - ML Kit Barcode Scanning
  - UI для сохранения QR кода при настройке (сканируй и сохрани)
  - Валидация отсканированного кода при пробуждении
  - Permission handling для камеры
- [x] Shake Phone Task:
  - Sensor API (TYPE_ACCELEROMETER)
  - Алгоритм детекции встряхивания (threshold + debounce)
  - Счётчик встряхиваний (например, 20 раз)
  - Визуализация прогресса с анимацией
- [x] Настройка задания в UI будильника
- [x] Базовая статистика:
  - Room entity `AlarmLog`
  - Логирование каждого срабатывания
  - Простая страница статистики: "Встал вовремя X из Y раз"
  - Процент успешных пробуждений

### AlarmLog Schema
```kotlin
@Entity(tableName = "alarm_logs")
data class AlarmLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long,
    val triggeredAt: Long,
    val completedAt: Long? = null,
    val wasCompleted: Boolean = false,
    val attempts: Int = 0,
    val mistakes: Int = 0,
    val taskType: TaskType,
    val snoozedCount: Int = 0
)
```

### Deliverables
- ✅ 3 типа заданий полностью работают
- ✅ Базовая статистика отображается
- ✅ MVP функционален и готов к использованию

---

## MVP 1.6: Quick Sleep Setup — Быстрый будильник в пару свайпов
**Срок:** 5-7 дней

### Задачи
- [x] Режим "Быстрый сон":
  - Слайдер выбора длительности (4-10ч, шаг 30 мин)
  - Расчёт с учётом циклов сна (90 мин)
  - Одно нажатие → будильник создан
  - Ночной UI (тёмная тема)
- [x] Режим "Когда лечь?":
  - Ввод времени пробуждения
  - Варианты отбоя по циклам (3-6 циклов)
  - Карточки с рекомендациями (оптимально / нормально / мало)
  - Создание будильника из карточки
- [x] `SleepCycleCalculator`: расчёт циклов, оптимальных времён
- [x] Настраиваемое время засыпания (default 15 мин)
- [x] Интеграция с главным экраном

### Deliverables
- ✅ Будильник ставится в 2 касания
- ✅ Циклы сна рассчитываются корректно
- ✅ UX интуитивный

---

# Iteration 2: Умные задания + Адаптивность

**Цель:** Сделать задания разнообразными и адаптивными под пользователя

**Длительность:** 2-3 недели

---

## Iteration 2.1: Advanced Math Tasks
**Срок:** 4-5 дней

### Задачи
- [x] Новые типы математики:
  - Смешанные операции: `(17 × 3) + 48 ÷ 6`
  - Числовые последовательности: `2, 4, 8, 16, ?`
  - Простые уравнения: `2x + 5 = 17, найти x`
- [x] Генераторы для каждого типа
- [x] Настройка сложности (Easy/Medium/Hard):
  - Easy: однозначные числа, простые операции
  - Medium: двузначные, 2 операции
  - Hard: трёхзначные, 3+ операций, скобки
- [x] Рандомизация типа внутри категории
- [x] UI для выбора сложности при создании будильника

### Math Generators
```kotlin
object MathTaskGenerator {
    fun generateMixedOperation(difficulty: Difficulty): MathProblem
    fun generateSequence(difficulty: Difficulty): SequenceProblem
    fun generateEquation(difficulty: Difficulty): EquationProblem
}
```

### Deliverables
- ✅ Математика не повторяется
- ✅ Вариативность высокая
- ✅ Сложность настраивается

---

## Iteration 2.2: New Task Types + Combinations
**Срок:** 5-7 дней

### Задачи
- [x] Memory Game Task:
  - Генерация последовательности 4-6 цветов/символов
  - Показ на 3-5 секунд
  - Воспроизведение пользователем
  - Визуализация: цветные кнопки с анимацией
- [x] Typing Challenge Task:
  - База мотивационных и сложных фраз
  - Детекция опечаток в реальном времени
  - Таймер (опционально)
  - Highlight ошибок
- [x] Multiple QR Codes Task:
  - Настройка 2-3 QR кодов
  - Последовательное сканирование
  - UI для управления списком кодов
  - Прогресс: "Отсканировано 2/3"
- [x] Комбинированные задания:
  - Настройка: "Math → QR" или "Math OR Shake"
  - Sequential vs Choice режимы
  - UI для конфигурации

### Deliverables
- ✅ 5+ типов заданий доступны
- ✅ Комбинации работают
- ✅ Каждое задание хорошо протестировано

---

## Iteration 2.3: Adaptive Difficulty + Enhanced Logging
**Срок:** 4-5 дней

### Задачи
- [x] Система адаптивной сложности:
  - Отслеживание статистики по типам заданий
  - Алгоритм адаптации: если >70% ошибок → снизить сложность
  - Если <20% ошибок и быстро решает → повысить
  - Settings toggle: "Автоматическая адаптация"
- [x] Расширенное логирование:
  - Добавить в `AlarmLog`: `taskDifficulty`, `timeToComplete`, `mistakesList`
  - История попыток с timestamps
- [x] "Важный день" режим:
  - Флаг `isImportantDay` на будильнике
  - Принудительная Hard сложность
  - Комбинация заданий
  - Нельзя snooze
  - UI toggle в настройках будильника
- [x] Статистика по заданиям:
  - Какой тип проходится лучше/хуже
  - Среднее время выполнения каждого типа
  - Визуализация в статистике

### Adaptive Algorithm
```kotlin
object DifficultyAdapter {
    fun calculateNewDifficulty(
        logs: List<AlarmLog>,
        currentDifficulty: Difficulty
    ): Difficulty {
        val recentLogs = logs.takeLast(10)
        val errorRate = recentLogs.map { it.mistakes.toFloat() / it.attempts }.average()
        
        return when {
            errorRate > 0.7 -> currentDifficulty.decrease()
            errorRate < 0.2 -> currentDifficulty.increase()
            else -> currentDifficulty
        }
    }
}
```

### Deliverables
- ✅ Система учится под пользователя
- ✅ Детальная статистика
- ✅ "Важный день" работает

---

# Iteration 2.5: UI/UX Polish & Design System

**Цель:** Привести всё в единый стиль, создать дизайн-систему. После этого приложение выглядит как продакшн-продукт.

**Длительность:** 7-10 дней

---

## Iteration 2.5: UI/UX Polish & Design System
**Срок:** 7-10 дней

### Задачи
- [x] Design tokens: цвета, типографика, spacing, shapes
- [x] App icon (adaptive, monochrome) + Splash screen
- [x] Light + Dark тема (+ AMOLED black опционально)
- [x] Кастомный шрифт (Google Fonts)
- [x] Анимации: transitions, toggle, swipe-to-delete, progress bars
- [x] Полировка всех экранов:
  - Карточки будильников с gradient
  - Активный будильник: immersive, пульсирующая кнопка
  - Quick Sleep: ночной UI с анимациями
  - Задания: feedback-анимации
- [x] Empty states с иллюстрациями
- [x] Accessibility: touch targets, contrast, TalkBack
- [x] Material You Dynamic Colors (Android 12+)

### Deliverables
- ✅ Готово для скриншотов Google Play
- ✅ Единый дизайн по всему приложению
- ✅ Accessibility проверена

---

# Iteration 3: Циклы сна + Аналитика

**Цель:** Помочь планировать сон и визуализировать паттерны

**Длительность:** 2-3 недели

---

## Iteration 3.1: Sleep Cycles Calculator
**Срок:** 3-4 дня

### Задачи
- [x] Экран Sleep Cycles Calculator
- [x] Режим "Когда лечь спать?":
  - Input: желаемое время пробуждения
  - Расчёт по формуле: wake_time - (n × 90min) - fall_asleep_time
  - Output: 3-4 варианта времени отбоя
  - Визуализация timeline с фазами сна
- [x] Обратный режим "Если лягу сейчас":
  - Input: текущее время
  - Output: оптимальные времена пробуждения
- [x] Настройка времени засыпания (default: 15 мин)
- [x] Кнопка "Создать будильник" из калькулятора
- [x] Быстрый доступ с главного экрана

### Sleep Cycle Logic
```kotlin
object SleepCycleCalculator {
    const val CYCLE_DURATION_MINUTES = 90
    const val DEFAULT_FALL_ASLEEP_MINUTES = 15
    
    fun calculateBedtimes(
        wakeTime: LocalTime,
        fallAsleepMinutes: Int = DEFAULT_FALL_ASLEEP_MINUTES
    ): List<LocalTime> {
        val cycles = listOf(6, 5, 4, 3) // Рекомендуемые циклы
        return cycles.map { cycleCount ->
            wakeTime
                .minusMinutes((cycleCount * CYCLE_DURATION_MINUTES).toLong())
                .minusMinutes(fallAsleepMinutes.toLong())
        }
    }
    
    fun calculateWakeTimes(
        bedtime: LocalTime,
        fallAsleepMinutes: Int = DEFAULT_FALL_ASLEEP_MINUTES
    ): List<LocalTime> {
        val fallAsleepTime = bedtime.plusMinutes(fallAsleepMinutes.toLong())
        return listOf(3, 4, 5, 6).map { cycles ->
            fallAsleepTime.plusMinutes((cycles * CYCLE_DURATION_MINUTES).toLong())
        }
    }
}
```

### Deliverables
- ✅ Калькулятор работает
- ✅ UI понятный и визуальный
- ✅ Можно создать будильник из калькулятора

---

## Iteration 3.2: Smart Alarm Window + Motion Detection
**Срок:** 6-8 дней

### Задачи
- [x] Smart Wake Window опция:
  - Toggle в настройках будильника
  - Настройка окна (10-30 мин)
  - Гарантированное пробуждение к концу окна
- [x] Motion Detection (опциональная фича):
  - Background Service с акселерометром
  - Алгоритм детекции движений
  - Определение активности (движение = REM фаза)
  - Будит в момент движения внутри окна
- [x] Fallback логика:
  - Если motion выключен → будит посередине окна
  - Если нет движения → будит в конце окна
- [x] Settings для motion tracking
- [x] Battery optimization handling
- [x] Notification: "Smart alarm отслеживает сон"

### Motion Detection
```kotlin
class MotionDetectionService : Service() {
    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val acceleration = sqrt(
                event.values[0].pow(2) + 
                event.values[1].pow(2) + 
                event.values[2].pow(2)
            )
            
            if (acceleration > MOVEMENT_THRESHOLD) {
                detectMovement()
            }
        }
    }
    
    private fun detectMovement() {
        // Если сейчас внутри smart window → разбудить
    }
}
```

### Deliverables
- ✅ Smart window работает
- ✅ Motion detection опционален
- ✅ Battery efficient

---

## Iteration 3.3: Advanced Statistics + Insights
**Срок:** 6-8 дней

### Задачи
- [x] Sleep Tracking:
  - Расчёт времени сна: bedtime → wake time
  - Ручной ввод bedtime (опционально)
  - Автоматика через Bedtime Mode (будет в Iteration 4)
- [x] Графики (библиотека: Vico или MPAndroidChart):
  - Bar chart: сон за неделю/месяц
  - Line chart: среднее время сна (скользящее среднее)
  - Scatter plot: регулярность (время отбоя vs время пробуждения)
  - Heatmap: качество сна по дням недели
- [x] Insights Engine:
  - SQL aggregations для анализа
  - Алгоритм генерации инсайтов:
    - "Оптимальная длительность сна: 7.5ч"
    - "По понедельникам сложнее просыпаешься на 30%"
    - "Стрик: 5 дней стабильного режима"
  - Показ на экране статистики
- [x] Напоминание "Пора спать":
  - Notification за N часов до будильника
  - Настройка: "за 8 часов"
  - Deeplink в Bedtime Mode
- [x] CSV Export базовый

### Statistics Schema
```kotlin
@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bedtime: Long,
    val wakeTime: Long,
    val sleepDuration: Long, // в минутах
    val quality: Int? = null, // 1-5 из morning check-in
    val notes: String? = null
)
```

### Deliverables
- ✅ Полноценная статистика
- ✅ Графики красивые и информативные
- ✅ Инсайты полезные

---

# Iteration 4: Вечерний режим + Ритуалы

**Цель:** Помочь готовиться ко сну и улучшить качество засыпания

**Длительность:** 2 недели

---

## Iteration 4.1: Bedtime Mode + Checklist
**Срок:** 4-5 дней

### Задачи
- [x] Экран Bedtime Mode
- [x] Вечерний чеклист:
  - Дефолтные пункты: "Зубы", "Зарядка", "Шторы", "Вода"
  - CRUD для кастомных пунктов
  - Checkbox UI с анимацией
  - Progress bar: "3/5 выполнено"
  - Сохранение состояния
- [x] Напоминание начать режим:
  - Notification за 30 мин до bedtime
  - Deeplink в Bedtime Mode
- [x] Разные чеклисты для будней/выходных
- [x] История выполнения

### Bedtime Schema
```kotlin
@Entity(tableName = "bedtime_checklists")
data class BedtimeChecklist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val items: List<String>, // Serialized JSON
    val isWeekday: Boolean = true
)

@Entity(tableName = "bedtime_logs")
data class BedtimeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val checklistId: Long,
    val completedItems: Set<String>,
    val wasFullyCompleted: Boolean
)
```

### Deliverables
- ✅ Bedtime Mode работает
- ✅ Чеклисты настраиваются
- ✅ История сохраняется

---

## Iteration 4.2: Sleep Timer + Breathing Exercises
**Срок:** 4-5 дней

### Задачи
- [x] Sleep Timer:
  - Запуск из Bedtime Mode
  - Countdown 15-20 мин (настраивается)
  - Gentle notification если не спишь
- [x] Breathing Exercises:
  - Техники: 4-7-8, Box Breathing (4-4-4-4)
  - Анимация: круг expand/contract
  - Haptic feedback синхронно с дыханием
  - Таймер: количество циклов или время
  - Ambient звук (опционально)
- [x] UI для выбора техники
- [x] Integration в Bedtime Mode

### Breathing Animation
```kotlin
@Composable
fun BreathingCircle(phase: BreathPhase) {
    val size by animateFloatAsState(
        targetValue = when(phase) {
            BreathPhase.INHALE -> 200f
            BreathPhase.HOLD -> 200f
            BreathPhase.EXHALE -> 100f
            BreathPhase.REST -> 100f
        },
        animationSpec = tween(durationMillis = phase.duration)
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = MaterialTheme.colorScheme.primary,
            radius = size
        )
    }
}
```

### Deliverables
- ✅ Sleep timer работает
- ✅ Дыхательные упражнения помогают расслабиться
- ✅ UX плавный

---

## Iteration 4.3: Sleep Sounds + Morning Check-in
**Срок:** 5-6 дней

### Задачи
- [x] Sleep Sounds Player:
  - Audio files: white/brown/pink noise, rain, ocean, forest
  - MediaPlayer с loop
  - Volume control
  - Auto-stop timer (30мин/1ч/∞)
  - Background playback (Foreground Service)
  - Notification controls
- [x] DND Integration (опционально):
  - Системный Do Not Disturb API
  - Блокировка приложений (требует AccessibilityService)
  - Screen dimming
  - Blue light filter (если возможно через Accessibility)
- [x] Morning Check-in:
  - Экран после отключения будильника
  - "Как выспался?" - scale 1-5 (emojis)
  - Опциональная заметка
  - Сохранение в AlarmLog: sleepQuality, notes
  - Корреляция в статистике
- [x] Мотивационная цитата утром

### Audio Assets
```
app/src/main/assets/sounds/
├── white_noise.mp3
├── brown_noise.mp3
├── pink_noise.mp3
├── rain.mp3
├── ocean.mp3
└── forest.mp3
```

### Deliverables
- ✅ Звуки помогают засыпать
- ✅ Morning check-in интегрирован
- ✅ Статистика учитывает качество сна

---

# Iteration 5: Gamification + Advanced Features

**Цель:** Долгосрочная мотивация и продвинутые возможности

**Длительность:** 2-3 недели

---

## Iteration 5.1: Achievements + Streaks
**Срок:** 4-5 дней

### Задачи
- [x] Achievement System:
  - Room entity для достижений
  - Список достижений:
    - "Первое пробуждение"
    - "7 дней подряд"
    - "30 дней подряд"
    - "100 пробуждений"
    - "Мастер математики"
    - "Рано встаёшь" (5 дней вставал до 7:00)
  - Логика проверки и unlocking
  - UI: красивые бейджи
- [x] Streak Calendar:
  - GitHub-style heatmap
  - Текущий streak
  - Лучший рекорд
  - Мягкое отношение к срывам
- [x] Progress Screen:
  - Общая статистика
  - Список достижений
  - Streaks

### Achievement Schema
```kotlin
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String, // "first_wake", "streak_7", etc
    val title: String,
    val description: String,
    val iconRes: Int,
    val unlockedAt: Long? = null
)

object AchievementChecker {
    fun checkAchievements(logs: List<AlarmLog>): List<Achievement>
}
```

### Deliverables
- ✅ Gamification работает
- ✅ Мотивация без токсичности
- ✅ UI вдохновляет

---

## Iteration 5.2: Claude API Integration + Smart Content
**Срок:** 6-8 дней

### Задачи
- [x] Claude API Setup:
  - Retrofit/Ktor для HTTP
  - Encrypted SharedPreferences для API key
  - Settings для ввода ключа
  - Error handling
- [x] AI Question Task:
  - Новый TaskType: AI_QUESTION
  - Генерация через API:
    - Общие знания
    - Логика
    - Философия (Камю/Сартр)
  - Проверка ответа через API
  - Retry logic
  - Fallback на другие задания если API down
  - Caching вопросов
- [x] Smart Morning Quotes:
  - Персонализированная цитата через API
  - Учёт статистики: "Ты спал 7.5ч - отлично!"
  - Offline cache
- [x] Опциональные AI Insights:
  - Анализ паттернов сна
  - Персональные рекомендации

### API Integration
```kotlin
interface ClaudeApiService {
    @POST("/v1/messages")
    suspend fun generateQuestion(
        @Body request: MessageRequest
    ): MessageResponse
    
    @POST("/v1/messages")
    suspend fun validateAnswer(
        @Body request: MessageRequest
    ): MessageResponse
}

data class MessageRequest(
    val model: String = "claude-sonnet-4-20250514",
    val max_tokens: Int = 1000,
    val messages: List<Message>
)
```

### Deliverables
- ✅ AI задания работают
- ✅ Контент динамический
- ✅ Graceful degradation при проблемах с API

---

## Iteration 5.3: Export, Profiles, Anti-Sabotage
**Срок:** 5-7 дней

### Задачи
- [x] Export & Backup:
  - CSV/JSON export всех данных
  - Выбор диапазона дат
  - Share через Intent
  - Google Drive backup (опционально)
  - Restore functionality
- [x] Alarm Profiles:
  - Профили: "Weekday", "Weekend", "Important", Custom
  - Каждый профиль: settings bundle
  - Быстрое переключение
  - Автоприменение по дням недели
- [x] Anti-Sabotage:
  - Lock Mode в Settings
  - Нельзя отключить будильник без пароля
  - Confirmation dialogs
  - Деструктивные действия логируются:
    - "5 попыток отключить раньше времени"
  - Warnings перед удалением
- [x] Связь с целями:
  - Экран "Why I do this"
  - Напоминания о здоровье
  - Интеграция с weight tracker (опционально)
- [x] Home Screen Widget:
  - Следующий будильник
  - Quick toggle
  - Быстрый доступ к Sleep Calculator

### Profile Schema
```kotlin
@Entity(tableName = "alarm_profiles")
data class AlarmProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val taskType: TaskType,
    val difficulty: Difficulty,
    val ringtoneUri: String?,
    val vibrate: Boolean,
    val smartWindow: Boolean,
    val snoozeEnabled: Boolean,
    val daysOfWeek: Set<DayOfWeek>? = null // Null = manual only
)
```

### Deliverables
- ✅ Export работает
- ✅ Профили удобны
- ✅ Anti-sabotage эффективен
- ✅ Widget полезен

---

# Post-MVP Ideas

Идеи для дальнейшего развития:

## Social Features
- Sleep buddy: будите друг друга
- Leaderboards среди друзей
- Shared achievements

## Health Integration
- Google Fit / Health Connect
- Wear OS companion app
- Heart rate monitoring (если есть девайс)

## ML & Advanced Analytics
- Предсказание оптимального времени сна
- Рекомендации на основе погоды, календаря
- Аномалии в паттернах сна

## Extended Features
- Meditation integration
- Sleep tracking через microphone (храп, движения)
- Smart home integration (свет, термостат)

---

# Success Metrics

**MVP успешен если:**
- ✅ Можешь заменить текущее приложение
- ✅ Невозможно проспать
- ✅ 3+ типа заданий работают стабильно

**Iteration 2 успешна если:**
- ✅ Задания не надоедают
- ✅ Адаптация работает
- ✅ Статистика информативна

**Iteration 3 успешна если:**
- ✅ Режим стабилизируется
- ✅ Понимаешь свои паттерны
- ✅ Качество сна улучшается

**Iteration 4 успешна если:**
- ✅ Ритуалы помогают засыпать
- ✅ Меньше времени до засыпания
- ✅ Morning check-in показывает улучшение качества

**Iteration 5 успешна если:**
- ✅ Долгосрочная мотивация работает
- ✅ AI контент полезен
- ✅ Приложение используешь постоянно

---

# Notes

- Каждая под-итерация должна заканчиваться работающим, протестированным кодом
- Commit после каждой под-итерации
- Тестирование на реальном устройстве обязательно
- Permissions handling критичен для Android
- Battery optimization - враг будильников, нужны workarounds

**Удачи в разработке! 🚀**
