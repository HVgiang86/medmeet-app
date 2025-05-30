package com.huongmt.medmeet.shared.core.datasource.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendService(
    @SerialName("recommended_service_ids")
    val services: List<String>,
)

