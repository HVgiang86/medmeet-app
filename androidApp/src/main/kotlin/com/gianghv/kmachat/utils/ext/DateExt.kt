package com.gianghv.kmachat.utils.ext

import com.gianghv.kmachat.constant.YYYY_MM_DD
import com.gianghv.kmachat.utils.DateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
