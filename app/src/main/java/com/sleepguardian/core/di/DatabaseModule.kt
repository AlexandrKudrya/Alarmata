package com.sleepguardian.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sleepguardian.core.database.AppDatabase
import com.sleepguardian.data.local.dao.AlarmDao
import com.sleepguardian.data.local.dao.AlarmLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS alarm_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    alarmId INTEGER NOT NULL,
                    triggeredAt INTEGER NOT NULL,
                    dismissedAt INTEGER,
                    taskType TEXT NOT NULL,
                    taskDifficulty TEXT NOT NULL,
                    totalProblems INTEGER NOT NULL,
                    solvedProblems INTEGER NOT NULL,
                    mistakes INTEGER NOT NULL,
                    snoozed INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (alarmId) REFERENCES alarms(id) ON DELETE CASCADE
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS index_alarm_logs_alarmId ON alarm_logs(alarmId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_alarm_logs_triggeredAt ON alarm_logs(triggeredAt)")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideAlarmDao(database: AppDatabase): AlarmDao {
        return database.alarmDao()
    }

    @Provides
    fun provideAlarmLogDao(database: AppDatabase): AlarmLogDao {
        return database.alarmLogDao()
    }
}
