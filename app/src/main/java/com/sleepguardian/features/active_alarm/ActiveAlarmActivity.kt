package com.sleepguardian.features.active_alarm

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sleepguardian.R
import com.sleepguardian.core.alarm.AlarmReceiver
import com.sleepguardian.core.alarm.AlarmService
import com.sleepguardian.core.locale.LocaleHelper
import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.domain.models.TaskType
import com.sleepguardian.features.tasks.engine.Task
import com.sleepguardian.features.tasks.engine.TaskData
import com.sleepguardian.features.tasks.engine.TaskFactory
import com.sleepguardian.features.tasks.engine.TaskProgress
import com.sleepguardian.presentation.theme.SleepGuardianTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActiveAlarmActivity : ComponentActivity() {

    private var alarmId: Long = -1L
    private var taskTypeName: String = "MATH"
    private var taskDifficultyName: String = "MEDIUM"

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Block back button so user can't dismiss alarm without action
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Intentionally empty — back button is disabled during alarm
            }
        })

        alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L)
        val label = intent.getStringExtra(AlarmReceiver.EXTRA_LABEL)
        val snoozeEnabled = intent.getBooleanExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, true)
        taskTypeName = intent.getStringExtra(AlarmReceiver.EXTRA_TASK_TYPE) ?: "MATH"
        taskDifficultyName = intent.getStringExtra(AlarmReceiver.EXTRA_TASK_DIFFICULTY) ?: "MEDIUM"

        val taskType = try { TaskType.valueOf(taskTypeName) } catch (_: Exception) { TaskType.MATH }
        val difficulty = try { Difficulty.valueOf(taskDifficultyName) } catch (_: Exception) { Difficulty.MEDIUM }
        val task = TaskFactory.create(taskType, difficulty)

        // Mute alarm sound/vibration — user sees the task, no need for noise
        muteAlarm()

        setContent {
            SleepGuardianTheme {
                ActiveAlarmScreen(
                    label = label,
                    snoozeEnabled = snoozeEnabled,
                    task = task,
                    onDismiss = { dismissAlarm() },
                    onSnooze = { snoozeAlarm() }
                )
            }
        }
    }

    private fun muteAlarm() {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_MUTE
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        startService(intent)
    }

    private fun dismissAlarm() {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        startService(intent)
        finish()
    }

    private fun snoozeAlarm() {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_SNOOZE
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        startService(intent)
        finish()
    }
}

@Composable
private fun ActiveAlarmScreen(
    label: String?,
    snoozeEnabled: Boolean,
    task: Task,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    var currentTaskData by remember { mutableStateOf<TaskData?>(null) }
    var progress by remember { mutableStateOf(task.getCurrentProgress()) }
    var taskCompleted by remember { mutableStateOf(false) }
    var answer by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Transparent) }
    var showFeedback by remember { mutableStateOf(false) }
    var mistakeShake by remember { mutableIntStateOf(0) }

    val focusRequester = remember { FocusRequester() }

    // Generate the first problem
    LaunchedEffect(Unit) {
        currentTaskData = task.generate()
    }

    // Auto-focus the text field after feedback clears
    LaunchedEffect(currentTaskData) {
        if (currentTaskData != null) {
            delay(100)
            try { focusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    // Animate feedback flash
    val bgColor by animateColorAsState(
        targetValue = if (showFeedback) feedbackColor.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "feedbackBg"
    )

    // Clear feedback after delay
    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(600)
            showFeedback = false
        }
    }

    fun submitAnswer() {
        val trimmed = answer.trim()
        if (trimmed.isEmpty()) return

        val correct = task.validate(trimmed)
        progress = task.getCurrentProgress()

        if (correct) {
            feedbackColor = Color(0xFF4CAF50) // green
            showFeedback = true
            answer = ""
            if (progress.current >= progress.total) {
                taskCompleted = true
            } else {
                currentTaskData = task.generate()
            }
        } else {
            feedbackColor = Color(0xFFF44336) // red
            showFeedback = true
            mistakeShake++
            answer = ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Clock icon + time
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!label.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (taskCompleted) {
                    // Task completed — show dismiss
                    Text(
                        text = stringResource(R.string.task_completed),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.task_result_mistakes, progress.mistakes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.alarm_dismiss),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    // Progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.task_progress, progress.current, progress.total),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (progress.mistakes > 0) {
                            Text(
                                text = stringResource(R.string.task_mistakes, progress.mistakes),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = if (progress.total > 0) progress.current.toFloat() / progress.total else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Math problem display
                    currentTaskData?.let { data ->
                        Text(
                            text = data.question,
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer input
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { newValue ->
                            // Allow only digits and minus sign
                            if (newValue.all { it.isDigit() || it == '-' }) {
                                answer = newValue
                            }
                        },
                        label = { Text(stringResource(R.string.task_answer_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submitAnswer() }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Check button
                    Button(
                        onClick = { submitAnswer() },
                        enabled = answer.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.task_check),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Snooze (available even during task, but dismiss is blocked)
                    if (snoozeEnabled) {
                        OutlinedButton(
                            onClick = onSnooze,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.alarm_snooze_with_time),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
