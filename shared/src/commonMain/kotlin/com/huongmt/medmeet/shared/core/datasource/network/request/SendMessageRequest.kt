package com.huongmt.medmeet.shared.core.datasource.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val content: String,
    @SerialName("is_user")
    val isUser: Boolean
)
