package com.huongmt.medmeet.shared.core.datasource.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("userName") val name: String? = null,
    @SerialName("dateOfBirth") val dob: String? = null,
    val gender: Int? = null,
    val province: String? = null,
    val district: String? = null,
    val commune: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
)
