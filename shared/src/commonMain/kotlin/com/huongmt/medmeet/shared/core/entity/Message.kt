package com.huongmt.medmeet.shared.core.entity

data class Message(
    val id: String,
    val userId: String,
    val conversationId: String,
    val isHuman: Boolean = true,
    val content: String? = null,
    val timestamp: Long? = 0L,
    val attachedFiles: List<AttachFile>? = emptyList()
) : MessageAbstract {
    override fun getRichText(): String = content ?: ""

    override fun getCopyableText(): String = content ?: ""
}

interface MessageAbstract {
    fun getRichText(): String?

    fun getCopyableText(): String?
}
