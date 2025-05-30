package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.AppNotification
import com.huongmt.medmeet.shared.core.entity.NotificationAction
import com.huongmt.medmeet.shared.core.entity.NotificationType
import com.huongmt.medmeet.shared.core.maper.toUser
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import com.huongmt.medmeet.shared.utils.ext.toLocalDateFromIso
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppNotificationResponse(
    @SerialName("_id")
    val id: String,
    val userId: String? = null,
    val action: String? = null,
    val details: String? = null,
    val updatedByUserId: String? = null,
    val entity: String? = null,
    val entityId: String? = null,
    val user: ProfileResponse? = null,
    val updatedByUser: ProfileResponse? = null,
    val createdAt: String? = null
) {
    fun toAppNotification(): AppNotification {
        val action = when (action?.lowercase()) {
            NotificationAction.CREATE.action.lowercase() -> NotificationAction.CREATE
            NotificationAction.UPDATE.action.lowercase() -> NotificationAction.UPDATE
            NotificationAction.DELETE.action.lowercase() -> NotificationAction.DELETE
            else -> null
        }

        val entity = when (entity?.lowercase()) {
            NotificationType.USER.route.lowercase() -> NotificationType.USER
            NotificationType.HEALTH_RECORD.route.lowercase() -> NotificationType.HEALTH_RECORD
            NotificationType.OTHER.route.lowercase() -> NotificationType.OTHER
            NotificationType.MEDICAL_CONSULTATION_HISTORY.route.lowercase() -> NotificationType.MEDICAL_CONSULTATION_HISTORY
            else -> null
        }

        val createdTime: LocalDateTime = createdAt?.toLocalDateFromIso() ?: nowDateTime()

        return AppNotification(
            id = id,
            action = action,
            details = details,
            updatedByUserId = updatedByUserId,
            type = entity,
            entityId = entityId,
            updatedByUser = updatedByUser?.toUser(),
            createdAt = createdTime
        )
    }
}
