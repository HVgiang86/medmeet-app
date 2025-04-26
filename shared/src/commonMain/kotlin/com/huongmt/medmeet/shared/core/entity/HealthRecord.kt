package com.huongmt.medmeet.shared.core.entity

data class HealthRecord(
    val id: String,
    val user: User,
    val bloodType: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val healthHistory: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
