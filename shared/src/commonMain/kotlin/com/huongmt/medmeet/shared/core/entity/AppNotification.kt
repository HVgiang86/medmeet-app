package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDateTime

data class AppNotification(
    val id: String,
    val action: NotificationAction? = null,
    val details: String? = null,
    val updatedByUserId: String? = null,
    val type: NotificationType? = null,
    val entityId: String? = null,
    val updatedByUser: User? = null,
    val createdAt: LocalDateTime? = null,
    val content: String? = null
)
