package com.habitstakes.app.data.db.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock

class DateTimeConverter {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.epochMilliseconds
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }
}
