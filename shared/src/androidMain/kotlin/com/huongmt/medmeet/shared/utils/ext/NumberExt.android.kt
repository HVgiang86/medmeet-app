package com.huongmt.medmeet.shared.utils.ext

import java.math.BigDecimal
import java.math.RoundingMode

actual fun Double.round(decimal: Int): Double {
    val rounded = BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN)
    return rounded.toDouble()
}
