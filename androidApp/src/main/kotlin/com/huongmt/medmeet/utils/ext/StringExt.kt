package com.huongmt.medmeet.utils.ext

import java.text.NumberFormat
import java.util.Locale

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.toLowerCase().capitalize() }
fun Double.formatPrice(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(this)} VND"
}
