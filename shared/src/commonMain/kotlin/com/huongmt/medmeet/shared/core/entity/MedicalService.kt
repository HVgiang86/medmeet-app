package com.huongmt.medmeet.shared.core.entity

import kotlinx.serialization.Serializable

@Serializable
data class MedicalService(
    val id: String,
    val name: String? = "",
    val currentPrice: Int? = 0,
    val type: MedicalServiceType = MedicalServiceType.SPECIALITY,
    val clinicId: String,
    val logo: String? = ""
)
