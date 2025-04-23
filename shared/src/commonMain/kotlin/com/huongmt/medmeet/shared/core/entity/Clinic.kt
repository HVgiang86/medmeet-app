package com.huongmt.medmeet.shared.core.entity

import kotlinx.serialization.Serializable

@Serializable
data class Clinic(
    val id: String,
    val name: String? = "",
    val email: String? = "",
    val hotline: String? = "",
    val address: String? = "",
    val status: ActiveStatus = ActiveStatus.ACTIVE,
    val description: String? = "",
    val logo: String? = ""
)
