package com.huongmt.medmeet.shared.utils.ext

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.toLowerCase().capitalize() }

fun Int.to2DigitString(): String = toString().padStart(2, '0')
