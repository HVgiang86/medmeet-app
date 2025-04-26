package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.utils.ext.toLocalDateFromIso
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    @SerialName("_id") val id: String,
    val title: String?,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("user_id") val userId: String
) {
    fun toConversation(): Conversation {
        var updateCreatedAt: LocalDateTime? = null
        var updateUpdatedAt: LocalDateTime? = null

        try {
            updateCreatedAt = createdAt?.toLocalDateFromIso()
            updateUpdatedAt = updatedAt?.toLocalDateFromIso()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Conversation(
            id = id,
            title = title,
            createdAt = updateCreatedAt,
            updatedAt = updateUpdatedAt
        )
    }
}
