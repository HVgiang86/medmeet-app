package com.huongmt.medmeet.shared.core.datasource.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateHealthRecord(
    @SerialName("_id")
    val id: String,
    val bloodType: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val healthHistory: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
