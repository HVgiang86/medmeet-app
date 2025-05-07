package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.Gender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val email: String? = "",
    val code: String? = null,
    @SerialName("_id") val id: String,
    @SerialName("userName") val name: String? = "",
    val avatar: String? = "",
    val gender: Int = Gender.MALE.value,
    @SerialName("dateOfBirth") val birthday: String? = "",
    val province: String? = "",
    val commune: String? = "",
    val district: String? = "",
    val address: String? = "",
    val phoneNumber: String? = "",
    val specialty: String? = "",
    val description: String? = "",
    val qualification: String? = "",
    val role: Int = 0
)
