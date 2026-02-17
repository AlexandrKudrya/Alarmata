package com.sleepguardian.core.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.sleepguardian.core.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var alarmScheduler: AlarmScheduler

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_STOP -> {
                stopAlarm(alarmId)
                return START_NOT_STICKY
            }
            ACTION_SNOOZE -> {
                snoozeAlarm(alarmId)
                return START_NOT_STICKY
            }
        }

        val shouldVibrate = intent?.getBooleanExtra(AlarmReceiver.EXTRA_VIBRATE, true) ?: true
        val ringtoneUri = intent?.getStringExtra(AlarmReceiver.EXTRA_RINGTONE_URI)
        val label = intent?.getStringExtra(AlarmReceiver.EXTRA_LABEL)
        val snoozeEnabled = intent?.getBooleanExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, true) ?: true

        val notification = notificationHelper.buildAlarmNotification(alarmId, label, snoozeEnabled)
        startForeground(alarmId.toInt(), notification.build())

        startRingtone(ringtoneUri)
        if (shouldVibrate) startVibration()

        // Auto-stop after 5 minutes
        serviceScope.launch {
            delay(5 * 60 * 1000L)
            stopAlarm(alarmId)
        }

        return START_NOT_STICKY
    }

    private fun startRingtone(uriString: String?) {
        try {
            val uri = if (uriString != null) Uri.parse(uriString)
            else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            // Fallback: try default notification sound
            try {
                val fallbackUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    setDataSource(this@AlarmService, fallbackUri)
                    isLooping = true
                    prepare()
                    start()
                }
            } catch (_: Exception) { }
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 500) // vibrate 500ms, pause 500ms, repeat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm(alarmId: Long) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        notificationHelper.cancelNotification(alarmId)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun snoozeAlarm(alarmId: Long) {
        stopAlarm(alarmId)
        // Snooze for 5 minutes
        val snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000L)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmReceiver.EXTRA_VIBRATE, true)
            putExtra(AlarmReceiver.EXTRA_SNOOZE_ENABLED, true)
        }
        val pi = android.app.PendingIntent.getBroadcast(
            this, alarmId.toInt(), intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(
            android.app.AlarmManager.AlarmClockInfo(snoozeTime, pi), pi
        )
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.sleepguardian.ACTION_STOP"
        const val ACTION_SNOOZE = "com.sleepguardian.ACTION_SNOOZE_SERVICE"
    }
}
