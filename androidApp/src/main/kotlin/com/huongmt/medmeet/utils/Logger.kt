package com.huongmt.medmeet.utils

import io.github.aakira.napier.Napier

object Logger {
    fun e(message: String?) {
        Napier.e(message ?: "")
    }

    fun e(
        tag: String?,
        message: String?,
    ) {
        Napier.e(tag = tag ?: "", message = message ?: "")
    }

    fun d(message: String?) {
        Napier.d(message ?: "")
    }

    fun d(
        tag: String?,
        message: String?,
    ) {
        Napier.d(tag = tag ?: "", message = message ?: "")
    }

    fun i(message: String?) {
        Napier.i(message ?: "")
    }

    fun i(
        tag: String?,
        message: String?,
    ) {
        Napier.i(tag = tag ?: "", message = message ?: "")
    }
}
