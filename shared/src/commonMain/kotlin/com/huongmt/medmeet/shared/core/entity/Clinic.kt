package com.huongmt.medmeet.shared.core.entity

data class Clinic(
    val id: String,
    val name: String? = "",
    val email: String? = "",
    val hotline: String? = "",
    val address: String? = "",
    val status: ActiveStatus = ActiveStatus.ACTIVE,
    val description: String? = "",
    val logo: String? = "",
    val schedules: List<ClinicSchedule> = emptyList()
)
