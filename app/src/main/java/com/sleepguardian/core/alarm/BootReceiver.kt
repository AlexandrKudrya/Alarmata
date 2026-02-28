package com.sleepguardian.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sleepguardian.data.local.dao.AlarmDao
import com.sleepguardian.data.repository.AlarmMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmDao: AlarmDao
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enabledAlarms = alarmDao.getEnabledAlarms()
                enabledAlarms.forEach { entity ->
                    val alarm = AlarmMapper.toDomain(entity)
                    alarmScheduler.schedule(alarm)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
