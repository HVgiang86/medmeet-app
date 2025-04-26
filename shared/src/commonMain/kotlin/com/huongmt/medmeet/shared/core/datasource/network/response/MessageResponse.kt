package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.entity.Message
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import com.huongmt.medmeet.shared.utils.ext.toLocalDateFromIso
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("_id") val id: String,

    @SerialName("is_user") val isHuman: Boolean = true,

    val content: String? = null,

    @SerialName("created_at") val createdAt: String?
) {
    fun toMessage(userId: String? = null, conversationId: String? = null): Message {
        var updateCreatedAt: LocalDateTime? = null

        try {
            updateCreatedAt = createdAt?.toLocalDateFromIso()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val now = nowDateTime()

        return Message(
            id = id,
            userId = userId ?: WholeApp.USER?.id ?: "",
            conversationId = conversationId ?: "",
            isHuman = isHuman,
            content = content,
            timestamp = updateCreatedAt ?: now
        )
    }
}
