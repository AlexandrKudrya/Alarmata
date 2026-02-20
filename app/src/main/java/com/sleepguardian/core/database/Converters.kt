package com.sleepguardian.core.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromDaysOfWeekString(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split(",")
    }

    @TypeConverter
    fun toDaysOfWeekString(value: List<String>): String {
        return value.joinToString(",")
    }
}
