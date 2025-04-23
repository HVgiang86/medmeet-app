package com.huongmt.medmeet.shared.utils.ext

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.toLowerCase().capitalize() }
