package com.sleepguardian.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sleepguardian.data.local.dao.AlarmDao
import com.sleepguardian.data.local.entities.AlarmEntity

@Database(
    entities = [
        AlarmEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        const val DATABASE_NAME = "sleep_guardian_db"
    }
}
