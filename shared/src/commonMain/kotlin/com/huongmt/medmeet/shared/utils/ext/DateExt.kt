package com.huongmt.medmeet.shared.utils.ext

import com.huongmt.medmeet.shared.config.YYYY_MM_DD
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun nowDate(): LocalDate {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun LocalDate.toIso8601String(): String {
    return "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
}

fun LocalDateTime.toIso8601String(): String {
    val date = "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
    val time = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"
    return "${date}T${time}Z"
}

fun LocalDate.toIso8601StringWithTime(): String {
    val dateString = "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
    return "${dateString}T00:00:00Z"
}

fun LocalDateTime.plusDate(date: Int): LocalDateTime {
    val timeZone = TimeZone.of("UTC")

    return this.toInstant(timeZone).plus(date, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)
}

/**
 * Converts a LocalDate to ISO-8601 formatted string with specified time components
 * Format: yyyy-MM-ddTHH:mm:ssZ
 */
fun LocalDate.toIso8601StringWithTime(hour: Int = 0, minute: Int = 0, second: Int = 0): String {
    val dateString = "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
    val timeString = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"
    return "${dateString}T${timeString}Z"
}

fun String.parseTimeToLocalDateTime(date: LocalDate = nowDate()): LocalDateTime? {
    return try {
        val parts = this.split(":")
        if (parts.size < 2) return null

        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val second = if (parts.size > 2) parts[2].toInt() else 0

        LocalDateTime(
            year = date.year,
            monthNumber = date.monthNumber,
            dayOfMonth = date.dayOfMonth,
            hour = hour,
            minute = minute,
            second = second,
            nanosecond = 0
        )
    } catch (e: Exception) {
        null
    }
}

fun nowDateTime(): LocalDateTime {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun String.toLocalDateFromIso(): LocalDateTime? {
    return try {
        // trim 'Z' if present
        val trimmedString = if (this.endsWith("Z")) this.substring(0, this.length - 1) else this

        LocalDateTime.parse(trimmedString)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun LocalDateTime.toDMY(): String {
    return "${this.dayOfMonth}/${this.monthNumber}/${this.year}"
}

fun LocalDate.toDMY(): String {
    return "${this.dayOfMonth}/${this.monthNumber}/${this.year}"
}

fun LocalDateTime.toHMS(): String {
    return "${this.hour.toString().padStart(2, '0')}:${
    this.minute.toString().padStart(2, '0')
    }:${this.second.toString().padStart(2, '0')}"
}

fun LocalDateTime.toHM(): String {
    return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
}

fun LocalDateTime.toHMSDMY(): String {
    return "${this.hour.toString().padStart(2, '0')}:${
    this.minute.toString().padStart(2, '0')
    }\n${this.dayOfMonth}/${this.monthNumber}"
}

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun Any.getCurrentDate(): LocalDate {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun Long.millisToDateString(format: String): String? {
    val localDate = this.toLocalDate()
    return getFormattedDate(localDate.toString(), YYYY_MM_DD, format)
}

fun String?.toLocalDate(): LocalDate? {
    if (this.isNullOrBlank()) return null

    return try {
        val instant = Instant.parse(this)
        instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    } catch (e: Exception) {
        null
    }
}

expect fun getFormattedDate(
    timestamp: String,
    format: String = "MMM dd, yyyy HH:mm:ss",
    outputFormat: String = "DD/MM/YYYY"
): String?
