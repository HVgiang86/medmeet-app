package com.huongmt.medmeet.utils.ext

import com.huongmt.medmeet.constant.YYYY_MM_DD
import com.huongmt.medmeet.utils.DateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun LocalDateTime.toDMY(): String {
    return "${this.dayOfMonth}/${this.monthNumber}/${this.year}"
}

fun LocalDate.toDMY(): String {
    return "${this.dayOfMonth}/${this.monthNumber}/${this.year}"
}

fun LocalDate.toDMY2(): String {
    return "${this.dayOfMonth}-${this.monthNumber}-${this.year}"
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

fun Any.getCurrentDate(): LocalDate {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun Long.millisToDateString(format: String): String? {
    val localDate = this.toLocalDate()
    return DateTime.getFormattedDate(localDate.toString(), YYYY_MM_DD, format)
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

fun LocalDate.format(format: String): String {
    return DateTime.getFormattedDate(this.toString(), YYYY_MM_DD, format) ?: this.toString()
}
