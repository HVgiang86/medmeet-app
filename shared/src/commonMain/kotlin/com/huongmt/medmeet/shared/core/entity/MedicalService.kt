package com.huongmt.medmeet.shared.core.entity

import kotlinx.serialization.Serializable

@Serializable
data class MedicalService(
    val id: String,
    val name: String? = "",
    val currentPrice: Long? = 0,
    val originalPrice: Long? = 0,
    val type: MedicalServiceType = MedicalServiceType.SPECIALITY,
    val clinicId: String
)
