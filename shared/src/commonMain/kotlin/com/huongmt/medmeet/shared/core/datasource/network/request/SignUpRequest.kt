package com.huongmt.medmeet.shared.core.datasource.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    @SerialName("userName") val name: String,
    @SerialName("dateOfBirth") val dob: String,
    val gender: Int,
    val province: String,
    val district: String,
    val address: String
)
