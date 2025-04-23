package com.huongmt.medmeet.shared.core.entity

data class Conversation(
    val id: String,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean = false
)
