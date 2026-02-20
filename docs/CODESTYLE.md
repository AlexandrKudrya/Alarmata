# Sleep Guardian — Code Style Guide

## Общие принципы

- Код пишется на **Kotlin**
- Форматирование — **ktlint** (стандартный Kotlin code style)
- Максимальная длина строки: **120 символов**
- Отступы: **4 пробела** (без табов)
- Файлы заканчиваются пустой строкой

## Именование

### Пакеты
- Всё в нижнем регистре, без подчёркиваний
- `com.sleepguardian.features.alarms.ui`

### Классы и интерфейсы
- PascalCase
- Интерфейсы **без** префикса `I`
- `AlarmRepository`, `MathTask`, `AlarmListViewModel`

### Функции и переменные
- camelCase
- Глаголы для функций: `getAlarms()`, `scheduleAlarm()`, `validateAnswer()`
- Существительные для свойств: `currentProgress`, `alarmList`

### Константы
- UPPER_SNAKE_CASE
- `const val MAX_SNOOZE_COUNT = 3`
- Объединять в `companion object` или `object Constants`

### Composable-функции
- PascalCase (как классы)
- `AlarmCard()`, `MathTaskView()`, `BreathingCircle()`

### Room Entities
- Суффикс `Entity`: `AlarmEntity`, `AlarmLogEntity`
- Таблицы в snake_case: `tableName = "alarm_logs"`

### Domain Models
- Без суффикса: `Alarm`, `AlarmLog`, `Task`
- Маппинг через отдельные Mapper-классы или extension-функции

### ViewModels
- Суффикс `ViewModel`: `AlarmListViewModel`
- UI State — sealed class/interface с суффиксом `UiState`: `AlarmListUiState`

### Use Cases
- Суффикс `UseCase`: `CreateAlarmUseCase`
- Один публичный метод: `operator fun invoke()`

## Архитектурные правила

### Слои (Clean Architecture)

```
presentation (features/) → domain/ → data/
```

- **domain/** не зависит от Android framework
- **data/** реализует интерфейсы из domain
- **features/** содержит UI + ViewModels, зависит от domain

### Зависимости между слоями

| Слой | Может зависеть от | Не может зависеть от |
|------|-------------------|---------------------|
| features/ | domain/ | data/ |
| domain/ | ничего | data/, features/, Android SDK |
| data/ | domain/ (interfaces) | features/ |
| core/ | Android SDK | features/, domain/, data/ |

### ViewModel

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val someUseCase: SomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExampleUiState>(ExampleUiState.Loading)
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()
}
```

- State через `StateFlow` (не LiveData)
- Один `_uiState` на ViewModel
- Side effects через `SharedFlow` (events) или Channel

### Composable

```kotlin
@Composable
fun AlarmCard(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ...
}
```

- `modifier` всегда последний параметр с дефолтом `Modifier`
- Callbacks как лямбды, не интерфейсы
- Preview-функции с `@Preview` аннотацией

### Repository

```kotlin
// domain/repositories/ — только interface
interface AlarmRepository {
    fun getAlarms(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Long
}

// data/repository/ — implementation
class AlarmRepositoryImpl @Inject constructor(
    private val dao: AlarmDao
) : AlarmRepository {
    // ...
}
```

## Kotlin-специфичные правила

### Null Safety
- Избегать `!!` — использовать `?.`, `?:`, `let`, `requireNotNull()`
- `!!` допустим только в тестах

### Coroutines
- `viewModelScope` для ViewModel
- `withContext(Dispatchers.IO)` для IO-операций
- Не использовать `GlobalScope`
- `Flow` для реактивных потоков данных

### Extension Functions
- Складывать в `core/utils/Extensions.kt` или по теме
- Не злоупотреблять — только для часто используемых паттернов

### Data Classes
- Для моделей данных — всегда data class
- Для UI State — sealed class/interface

```kotlin
sealed interface AlarmListUiState {
    data object Loading : AlarmListUiState
    data class Success(val alarms: List<Alarm>) : AlarmListUiState
    data class Error(val message: String) : AlarmListUiState
}
```

## Комментарии

- **Не** комментировать очевидный код
- KDoc (`/** */`) для публичных API и сложной бизнес-логики
- TODO формат: `// TODO: описание задачи`
- FIXME формат: `// FIXME: описание проблемы`

## Тестирование

### Именование тестов
```kotlin
@Test
fun `alarm is scheduled when created`() { ... }

@Test
fun `validate returns false for incorrect answer`() { ... }
```

- Бэктики с человекочитаемым описанием
- Паттерн: `действие + ожидаемый результат` или `условие + ожидаемое поведение`

### Структура теста
- Arrange / Act / Assert (AAA)
- Один assert на тест (предпочтительно)

## Инструменты

- **ktlint** — автоматическое форматирование
- **detekt** — статический анализ (опционально)
- **Android Lint** — специфичные Android-проверки

## Git

### Commit Messages
Формат: `тип: краткое описание`

Типы:
- `feat:` — новая функциональность
- `fix:` — исправление бага
- `refactor:` — рефакторинг без изменения поведения
- `docs:` — изменения в документации
- `test:` — добавление/изменение тестов
- `chore:` — прочие изменения (зависимости, конфиги)

Примеры:
```
feat: add math task generation for alarm
fix: alarm not triggering on Samsung devices
refactor: extract task validation logic to use case
docs: update iteration 2.1 status
test: add unit tests for SleepCycleCalculator
chore: bump Compose BOM to 2024.01
```

### Branches
- `main` — production
- `develop` — integration
- `feature/mvp-1.1-project-setup` — фича
- `fix/alarm-not-triggering` — фикс
