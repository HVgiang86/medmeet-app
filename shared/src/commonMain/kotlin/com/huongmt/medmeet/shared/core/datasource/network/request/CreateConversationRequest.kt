package com.huongmt.medmeet.shared.core.datasource.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequest(
    @SerialName("user_id")
    val uid: String,
    val title: String
)
