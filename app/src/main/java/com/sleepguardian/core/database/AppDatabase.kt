package com.sleepguardian.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sleepguardian.data.local.dao.AlarmDao
import com.sleepguardian.data.local.dao.AlarmLogDao
import com.sleepguardian.data.local.entities.AlarmEntity
import com.sleepguardian.data.local.entities.AlarmLogEntity

@Database(
    entities = [
        AlarmEntity::class,
        AlarmLogEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun alarmLogDao(): AlarmLogDao

    companion object {
        const val DATABASE_NAME = "sleep_guardian_db"
    }
}
