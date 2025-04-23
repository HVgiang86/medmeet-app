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
    val province: String? = "",
    val district: String? = "",
    val address: String? = "",
    val role: Int = 0
)
