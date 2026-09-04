package com.habitstakes.app.data.db.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateTimeConverter {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.epochSeconds?.times(1000)
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }
}
