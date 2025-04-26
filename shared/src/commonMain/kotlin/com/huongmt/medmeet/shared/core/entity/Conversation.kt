package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDateTime

data class Conversation(
    val id: String,
    val title: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
