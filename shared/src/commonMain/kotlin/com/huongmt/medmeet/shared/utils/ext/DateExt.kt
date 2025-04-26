package com.huongmt.medmeet.shared.utils.ext

import com.huongmt.medmeet.shared.config.YYYY_MM_DD
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun nowDate(): LocalDate {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun nowDateTime(): LocalDateTime {
    val instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun String.toLocalDateFromIso(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
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
