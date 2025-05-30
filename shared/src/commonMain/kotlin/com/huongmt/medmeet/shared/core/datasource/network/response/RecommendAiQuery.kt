package com.huongmt.medmeet.shared.core.datasource.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendAiQuery(
    @SerialName("content")
    val queries: List<String>,
)
