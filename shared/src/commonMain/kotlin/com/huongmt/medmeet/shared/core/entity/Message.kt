package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDateTime

data class Message(
    val id: String,
    val userId: String,
    val conversationId: String,
    val isHuman: Boolean = true,
    val content: String? = null,
    val timestamp: LocalDateTime
)
