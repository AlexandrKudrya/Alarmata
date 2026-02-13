# Sleep Guardian - Technical Architecture

> Техническая архитектура Android-приложения для умного пробуждения

---

## Technology Stack

### Core Technologies
- **Language:** Kotlin 1.9+
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Build System:** Gradle 8.2+ with Kotlin DSL

### UI Framework
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Latest Material Design
- **Compose Navigation** - Type-safe navigation
- **Accompanist** - Compose utilities (permissions, system UI)

### Architecture Components
- **Architecture:** Clean Architecture + MVVM
- **DI:** Hilt (Dagger)
- **Database:** Room 2.6+
- **Async:** Kotlin Coroutines + Flow
- **Lifecycle:** ViewModel, LiveData

### System Integration
- **AlarmManager** - Precise alarm scheduling
- **WorkManager** - Backup alarm mechanism
- **Foreground Service** - Wake lock and alarm ringing
- **Notifications** - Reminders and bedtime mode
- **Sensors API** - Motion detection, shake detection

### Media & Camera
- **CameraX** - QR code scanning
- **ML Kit** - Barcode detection
- **MediaPlayer** - Ringtones and sleep sounds
- **ExoPlayer** (optional) - Advanced audio playback

### Networking
- **Retrofit** - REST API client for Claude API
- **OkHttp** - HTTP client with interceptors
- **Gson/Kotlinx Serialization** - JSON parsing

### Data Visualization
- **Vico** or **MPAndroidChart** - Charts and graphs
- Custom Canvas drawing for specific visualizations

### Security
- **EncryptedSharedPreferences** - Secure storage for API keys
- **Biometric API** - Optional fingerprint/face unlock

### Testing
- **JUnit 5** - Unit testing
- **MockK** - Mocking framework
- **Turbine** - Flow testing
- **Compose Testing** - UI testing
- **Espresso** - Integration testing (optional)

---

## Project Structure

```
com.sleepguardian/
├── app/
│   ├── SleepGuardianApp.kt              # Application class
│   └── MainActivity.kt                  # Single activity
│
├── core/
│   ├── di/                              # Dependency injection modules
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   │
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt                # Type converters
│   │   └── migrations/
│   │
│   ├── alarm/
│   │   ├── AlarmScheduler.kt            # AlarmManager wrapper
│   │   ├── AlarmReceiver.kt             # BroadcastReceiver
│   │   ├── WakeLockService.kt           # Foreground service
│   │   └── BootReceiver.kt              # Boot completed receiver
│   │
│   ├── notifications/
│   │   └── NotificationHelper.kt
│   │
│   ├── utils/
│   │   ├── DateTimeUtils.kt
│   │   ├── PermissionUtils.kt
│   │   └── Extensions.kt
│   │
│   └── constants/
│       └── Constants.kt
│
├── domain/
│   ├── models/
│   │   ├── Alarm.kt
│   │   ├── AlarmLog.kt
│   │   ├── Task.kt
│   │   ├── Achievement.kt
│   │   ├── SleepSession.kt
│   │   └── BedtimeChecklist.kt
│   │
│   ├── repositories/
│   │   ├── AlarmRepository.kt           # Interfaces
│   │   ├── StatisticsRepository.kt
│   │   ├── TaskRepository.kt
│   │   └── BedtimeRepository.kt
│   │
│   └── usecases/
│       ├── alarm/
│       │   ├── CreateAlarmUseCase.kt
│       │   ├── DeleteAlarmUseCase.kt
│       │   └── UpdateAlarmUseCase.kt
│       │
│       ├── tasks/
│       │   ├── GenerateTaskUseCase.kt
│       │   └── ValidateTaskUseCase.kt
│       │
│       └── statistics/
│           ├── GetSleepStatisticsUseCase.kt
│           └── GenerateInsightsUseCase.kt
│
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── AlarmDao.kt
│   │   │   ├── AlarmLogDao.kt
│   │   │   ├── AchievementDao.kt
│   │   │   └── BedtimeDao.kt
│   │   │
│   │   └── entities/
│   │       ├── AlarmEntity.kt
│   │       ├── AlarmLogEntity.kt
│   │       └── ...
│   │
│   ├── remote/
│   │   ├── api/
│   │   │   └── ClaudeApiService.kt
│   │   │
│   │   ├── dto/
│   │   │   ├── MessageRequest.kt
│   │   │   └── MessageResponse.kt
│   │   │
│   │   └── interceptors/
│   │       └── AuthInterceptor.kt
│   │
│   └── repository/                      # Implementations
│       ├── AlarmRepositoryImpl.kt
│       ├── StatisticsRepositoryImpl.kt
│       └── ...
│
├── features/
│   ├── alarms/
│   │   ├── ui/
│   │   │   ├── AlarmListScreen.kt
│   │   │   ├── AddEditAlarmScreen.kt
│   │   │   └── components/
│   │   │
│   │   └── viewmodel/
│   │       ├── AlarmListViewModel.kt
│   │       └── AddEditAlarmViewModel.kt
│   │
│   ├── active_alarm/
│   │   ├── ui/
│   │   │   ├── ActiveAlarmScreen.kt
│   │   │   └── components/
│   │   │       ├── MathTaskView.kt
│   │   │       ├── QRScannerView.kt
│   │   │       └── ShakeTaskView.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   └── ActiveAlarmViewModel.kt
│   │   │
│   │   └── ActiveAlarmActivity.kt       # Separate activity for wake lock
│   │
│   ├── tasks/
│   │   ├── engine/
│   │   │   ├── Task.kt                  # Interface
│   │   │   ├── TaskFactory.kt
│   │   │   └── TaskProgress.kt
│   │   │
│   │   └── implementations/
│   │       ├── MathTask.kt
│   │       ├── QRCodeTask.kt
│   │       ├── ShakeTask.kt
│   │       ├── MemoryTask.kt
│   │       ├── TypingTask.kt
│   │       └── AIQuestionTask.kt
│   │
│   ├── statistics/
│   │   ├── ui/
│   │   │   ├── StatisticsScreen.kt
│   │   │   └── components/
│   │   │       ├── SleepChartView.kt
│   │   │       ├── InsightCard.kt
│   │   │       └── HeatmapView.kt
│   │   │
│   │   └── viewmodel/
│   │       └── StatisticsViewModel.kt
│   │
│   ├── sleep_cycles/
│   │   ├── ui/
│   │   │   └── SleepCyclesCalculatorScreen.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   └── SleepCyclesViewModel.kt
│   │   │
│   │   └── calculator/
│   │       └── SleepCycleCalculator.kt
│   │
│   ├── bedtime/
│   │   ├── ui/
│   │   │   ├── BedtimeModeScreen.kt
│   │   │   ├── BreathingExerciseScreen.kt
│   │   │   └── SleepSoundsScreen.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   └── BedtimeViewModel.kt
│   │   │
│   │   └── service/
│   │       └── SleepSoundsService.kt
│   │
│   ├── achievements/
│   │   ├── ui/
│   │   │   └── AchievementsScreen.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   └── AchievementsViewModel.kt
│   │   │
│   │   └── checker/
│   │       └── AchievementChecker.kt
│   │
│   └── settings/
│       ├── ui/
│       │   └── SettingsScreen.kt
│       │
│       └── viewmodel/
│           └── SettingsViewModel.kt
│
└── widget/
    ├── AlarmWidget.kt
    └── AlarmWidgetReceiver.kt
```

---

## Architecture Layers

### 1. Presentation Layer (UI + ViewModel)

**Responsibilities:**
- User interface with Jetpack Compose
- Handle user interactions
- Observe data from ViewModels
- Navigate between screens

**Pattern:** MVVM (Model-View-ViewModel)

```kotlin
@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AlarmListUiState>(AlarmListUiState.Loading)
    val uiState: StateFlow<AlarmListUiState> = _uiState.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    private fun loadAlarms() {
        viewModelScope.launch {
            getAlarmsUseCase()
                .catch { e -> _uiState.value = AlarmListUiState.Error(e.message) }
                .collect { alarms -> _uiState.value = AlarmListUiState.Success(alarms) }
        }
    }
}
```

---

### 2. Domain Layer (Business Logic)

**Responsibilities:**
- Business rules
- Use cases (single responsibility operations)
- Domain models (platform-independent)
- Repository interfaces

**Key Principles:**
- No Android dependencies
- Pure Kotlin
- Testable without instrumentation

```kotlin
class CreateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm): Result<Long> = try {
        val alarmId = repository.insertAlarm(alarm)
        alarmScheduler.schedule(alarm.copy(id = alarmId))
        Result.success(alarmId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

### 3. Data Layer (Repositories)

**Responsibilities:**
- Data sources coordination (local DB, remote API)
- Caching strategy
- Data mapping (Entity ↔ Domain Model)

```kotlin
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val mapper: AlarmMapper
) : AlarmRepository {
    
    override fun getAllAlarms(): Flow<List<Alarm>> =
        alarmDao.getAllAlarms()
            .map { entities -> entities.map(mapper::toDomain) }
    
    override suspend fun insertAlarm(alarm: Alarm): Long =
        alarmDao.insert(mapper.toEntity(alarm))
}
```

---

## Core Components Deep Dive

### AlarmScheduler

Обёртка над AlarmManager для надёжного планирования.

```kotlin
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    
    fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = calculateNextTriggerTime(alarm)
        
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
    
    private fun calculateNextTriggerTime(alarm: Alarm): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // If time has passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Find next enabled day of week
        while (!alarm.daysOfWeek.contains(calendar.get(Calendar.DAY_OF_WEEK).toDayOfWeek())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return calendar.timeInMillis
    }
}
```

**Key Points:**
- `setExactAndAllowWhileIdle` для точного срабатывания даже в Doze mode
- PendingIntent с FLAG_IMMUTABLE для безопасности
- Учёт дней недели при расчёте следующего срабатывания

---

### WakeLockService

Foreground Service для удержания экрана включённым.

```kotlin
class WakeLockService : Service() {
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1) ?: -1
        
        // Acquire wake lock
        acquireWakeLock()
        
        // Show notification
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Start ringing
        startRinging(alarmId)
        
        // Launch fullscreen activity
        launchAlarmActivity(alarmId)
        
        return START_STICKY
    }
    
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or 
            PowerManager.ACQUIRE_CAUSES_WAKEUP or
            PowerManager.ON_AFTER_RELEASE,
            "SleepGuardian::WakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // 10 minutes max
        }
    }
    
    private fun startRinging(alarmId: Long) {
        // Get alarm settings from repository
        val alarm = getAlarm(alarmId)
        
        // Start vibration
        if (alarm.vibrate) {
            startVibration()
        }
        
        // Play ringtone with gradual volume increase
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, alarm.ringtoneUri ?: getDefaultRingtone())
            isLooping = true
            setVolume(0f, 0f)
            prepare()
            start()
            
            // Gradually increase volume
            animateVolume()
        }
    }
    
    private fun animateVolume() {
        viewModelScope.launch {
            for (i in 0..10) {
                delay(1000)
                val volume = i / 10f
                mediaPlayer?.setVolume(volume, volume)
            }
        }
    }
    
    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        vibrator?.cancel()
        wakeLock?.release()
        super.onDestroy()
    }
}
```

**Critical Permissions:**
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

---

### ActiveAlarmActivity

Отдельная Activity для полноэкранного показа будильника.

```kotlin
class ActiveAlarmActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        // Disable back button
        onBackPressedDispatcher.addCallback(this) {
            // Do nothing - can't dismiss without completing task
        }
        
        setContent {
            SleepGuardianTheme {
                ActiveAlarmScreen(
                    alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1),
                    onAlarmDismissed = {
                        stopService(Intent(this, WakeLockService::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
```

**Manifest Declaration:**
```xml
<activity
    android:name=".features.active_alarm.ActiveAlarmActivity"
    android:excludeFromRecents="true"
    android:launchMode="singleInstance"
    android:showWhenLocked="true"
    android:turnScreenOn="true" />
```

---

### Task Engine

Система для разных типов заданий.

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

class TaskFactory @Inject constructor(
    private val mathTaskProvider: Provider<MathTask>,
    private val qrCodeTaskProvider: Provider<QRCodeTask>,
    private val shakeTaskProvider: Provider<ShakeTask>
) {
    fun create(type: TaskType, difficulty: Difficulty): Task = when(type) {
        TaskType.MATH -> mathTaskProvider.get().apply { setDifficulty(difficulty) }
        TaskType.QR_CODE -> qrCodeTaskProvider.get()
        TaskType.SHAKE -> shakeTaskProvider.get()
        // ...
    }
}
```

**Example: MathTask**

```kotlin
class MathTask @Inject constructor() : Task {
    
    private var difficulty: Difficulty = Difficulty.MEDIUM
    private val totalProblems = 5
    private var currentProblem = 0
    private var mistakes = 0
    private var currentAnswer: Int = 0
    
    override fun generate(): TaskData {
        val (question, answer) = when(difficulty) {
            Difficulty.EASY -> generateEasyProblem()
            Difficulty.MEDIUM -> generateMediumProblem()
            Difficulty.HARD -> generateHardProblem()
        }
        
        currentAnswer = answer
        currentProblem++
        
        return TaskData(
            question = question,
            metadata = mapOf(
                "problemNumber" to currentProblem,
                "totalProblems" to totalProblems
            )
        )
    }
    
    override fun validate(answer: String): Boolean {
        val isCorrect = answer.toIntOrNull() == currentAnswer
        if (!isCorrect) mistakes++
        return isCorrect
    }
    
    override fun getCurrentProgress() = TaskProgress(
        current = currentProblem,
        total = totalProblems,
        mistakes = mistakes
    )
    
    private fun generateMediumProblem(): Pair<String, Int> {
        val a = Random.nextInt(10, 99)
        val b = Random.nextInt(10, 99)
        val c = Random.nextInt(2, 9)
        
        val operation = Random.nextInt(0, 3)
        return when(operation) {
            0 -> "($a + $b) × $c" to (a + b) * c
            1 -> "($a - $b) + ${Random.nextInt(10, 50)}" to (a - b) + Random.nextInt(10, 50)
            else -> "$a × $c + $b" to a * c + b
        }
    }
}
```

---

## Database Schema

### Room Database

```kotlin
@Database(
    entities = [
        AlarmEntity::class,
        AlarmLogEntity::class,
        SleepSessionEntity::class,
        BedtimeChecklistEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun alarmLogDao(): AlarmLogDao
    abstract fun sleepSessionDao(): SleepSessionDao
    abstract fun bedtimeDao(): BedtimeDao
    abstract fun achievementDao(): AchievementDao
}
```

### Entities

```kotlin
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: String, // Serialized Set<DayOfWeek>
    val isEnabled: Boolean,
    val taskType: String,
    val taskDifficulty: String,
    val ringtoneUri: String?,
    val label: String?,
    val vibrate: Boolean,
    val smartWindow: Boolean,
    val smartWindowMinutes: Int,
    val snoozeEnabled: Boolean,
    val isImportantDay: Boolean,
    val createdAt: Long
)

@Entity(
    tableName = "alarm_logs",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long,
    val triggeredAt: Long,
    val completedAt: Long?,
    val wasCompleted: Boolean,
    val attempts: Int,
    val mistakes: Int,
    val taskType: String,
    val taskDifficulty: String,
    val timeToComplete: Long?,
    val snoozedCount: Int,
    val sleepQuality: Int?,
    val notes: String?
)

@Entity(tableName = "sleep_sessions")
data class SleepSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bedtime: Long,
    val wakeTime: Long,
    val sleepDuration: Long, // minutes
    val quality: Int?,
    val notes: String?,
    val wasTrackedByMotion: Boolean
)
```

### Type Converters

```kotlin
class Converters {
    @TypeConverter
    fun fromDaysOfWeek(value: Set<DayOfWeek>): String {
        return value.joinToString(",") { it.name }
    }
    
    @TypeConverter
    fun toDaysOfWeek(value: String): Set<DayOfWeek> {
        return value.split(",")
            .filter { it.isNotBlank() }
            .map { DayOfWeek.valueOf(it) }
            .toSet()
    }
    
    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name
    
    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)
}
```

---

## Dependency Injection (Hilt)

### Modules

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sleep_guardian_db"
        )
            .fallbackToDestructiveMigration() // TODO: Add proper migrations
            .build()
    }
    
    @Provides
    fun provideAlarmDao(database: AppDatabase) = database.alarmDao()
    
    @Provides
    fun provideAlarmLogDao(database: AppDatabase) = database.alarmLogDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideClaudeApi(retrofit: Retrofit): ClaudeApiService {
        return retrofit.create(ClaudeApiService::class.java)
    }
}
```

---

## Testing Strategy

### Unit Tests

```kotlin
class MathTaskTest {
    
    private lateinit var mathTask: MathTask
    
    @BeforeEach
    fun setup() {
        mathTask = MathTask()
    }
    
    @Test
    fun `generate creates valid problem`() {
        val taskData = mathTask.generate()
        
        assertNotNull(taskData.question)
        assertTrue(taskData.question.isNotEmpty())
    }
    
    @Test
    fun `validate returns true for correct answer`() {
        val taskData = mathTask.generate()
        val correctAnswer = extractAnswer(taskData)
        
        assertTrue(mathTask.validate(correctAnswer.toString()))
    }
    
    @Test
    fun `validate returns false for incorrect answer`() {
        mathTask.generate()
        
        assertFalse(mathTask.validate("999999"))
    }
    
    @Test
    fun `progress tracks correctly`() {
        repeat(3) { mathTask.generate() }
        
        val progress = mathTask.getCurrentProgress()
        
        assertEquals(3, progress.current)
        assertEquals(5, progress.total)
    }
}
```

### ViewModel Tests

```kotlin
@ExperimentalCoroutinesTest
class AlarmListViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private val getAlarmsUseCase: GetAlarmsUseCase = mockk()
    private val toggleAlarmUseCase: ToggleAlarmUseCase = mockk()
    
    private lateinit var viewModel: AlarmListViewModel
    
    @BeforeEach
    fun setup() {
        every { getAlarmsUseCase() } returns flowOf(emptyList())
        
        viewModel = AlarmListViewModel(getAlarmsUseCase, toggleAlarmUseCase)
    }
    
    @Test
    fun `initial state is loading`() = runTest {
        val initialState = viewModel.uiState.value
        
        assertTrue(initialState is AlarmListUiState.Loading)
    }
    
    @Test
    fun `loads alarms successfully`() = runTest {
        val alarms = listOf(
            Alarm(id = 1, hour = 7, minute = 0)
        )
        every { getAlarmsUseCase() } returns flowOf(alarms)
        
        viewModel = AlarmListViewModel(getAlarmsUseCase, toggleAlarmUseCase)
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is AlarmListUiState.Success)
        assertEquals(1, (state as AlarmListUiState.Success).alarms.size)
    }
}
```

### Integration Tests

```kotlin
@HiltAndroidTest
class AlarmSchedulingIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var alarmScheduler: AlarmScheduler
    
    @Inject
    lateinit var alarmRepository: AlarmRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun alarm_is_scheduled_when_created() = runTest {
        val alarm = Alarm(
            hour = 7,
            minute = 30,
            daysOfWeek = setOf(DayOfWeek.MONDAY)
        )
        
        val alarmId = alarmRepository.insertAlarm(alarm)
        alarmScheduler.schedule(alarm.copy(id = alarmId))
        
        // Verify alarm is scheduled in AlarmManager
        val alarmManager = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .getSystemService(AlarmManager::class.java)
        
        assertNotNull(alarmManager.nextAlarmClock)
    }
}
```

---

## Performance Considerations

### Database Optimization

```kotlin
// Use indices for frequent queries
@Entity(
    tableName = "alarms",
    indices = [
        Index(value = ["isEnabled"]),
        Index(value = ["hour", "minute"])
    ]
)
data class AlarmEntity(...)

// Pagination for large datasets
@Query("SELECT * FROM alarm_logs ORDER BY triggeredAt DESC LIMIT :limit OFFSET :offset")
fun getAlarmLogsPaged(limit: Int, offset: Int): List<AlarmLogEntity>
```

### Battery Optimization

```kotlin
// Request battery optimization exemption
fun requestBatteryOptimizationExemption(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        activity.startActivity(intent)
    }
}
```

### Compose Performance

```kotlin
// Use remember and derivedStateOf
@Composable
fun AlarmListScreen(viewModel: AlarmListViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    val sortedAlarms = remember(uiState) {
        derivedStateOf {
            when(val state = uiState) {
                is AlarmListUiState.Success -> state.alarms.sortedBy { it.hour * 60 + it.minute }
                else -> emptyList()
            }
        }
    }
    
    LazyColumn {
        items(sortedAlarms.value) { alarm ->
            AlarmCard(alarm = alarm)
        }
    }
}
```

---

## Security

### API Key Storage

```kotlin
object SecureStorage {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_API_KEY = "claude_api_key"
    
    fun saveApiKey(context: Context, apiKey: String) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        val encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        encryptedPrefs.edit()
            .putString(KEY_API_KEY, apiKey)
            .apply()
    }
    
    fun getApiKey(context: Context): String? {
        // Similar decryption logic
    }
}
```

---

## Build Configuration

### Gradle (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.sleepguardian"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.sleepguardian"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    
    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // Charts
    implementation("com.patrykandpatrick.vico:compose:1.12.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

---

Это детальная техническая архитектура. Далее создам документ по CI/CD.
