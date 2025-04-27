package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDateTime

data class ClinicSchedule(
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
