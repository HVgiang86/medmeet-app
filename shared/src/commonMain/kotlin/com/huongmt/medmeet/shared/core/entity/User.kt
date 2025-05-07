package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String? = "",
    val code: String? = null,
    val id: String,
    val name: String? = "",
    val avatar: String? = "",
    val gender: Gender = Gender.MALE,
    val birthday: LocalDate? = null,
    val phoneNumber: String? = "",
    val province: String? = "",
    val district: String? = "",
    val commune: String? = "",
    val address: String? = "",
    val specialty: String? = "",
    val description: String? = "",
    val qualification: String? = "",
    val role: Int = 0
)
