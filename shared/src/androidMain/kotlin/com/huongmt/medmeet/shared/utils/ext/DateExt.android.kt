package com.huongmt.medmeet.shared.utils.ext

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

actual fun getFormattedDate(
    timestamp: String,
    format: String,
    outputFormat: String
): String? {
    val dateFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
    dateFormatter.timeZone = TimeZone.getDefault()

    val parser = SimpleDateFormat(format, Locale.getDefault())
    parser.timeZone = TimeZone.getDefault()

    val date = parser.parse(timestamp)
    dateFormatter.timeZone = TimeZone.getDefault()
    return date?.let { dateFormatter.format(it) }
}
