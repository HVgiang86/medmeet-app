package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SignUpData(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val dob: LocalDate,
    val gender: Gender,
    val province: String,
    val district: String,
    val commune: String,
    val address: String
)
