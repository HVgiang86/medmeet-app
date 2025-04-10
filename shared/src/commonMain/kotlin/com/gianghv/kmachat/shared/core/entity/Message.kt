package com.gianghv.kmachat.shared.core.entity

data class Message(
    val id: String,
    val userId: String,
    val isHuman: Boolean = true,
    val content: String? = null,
    val timestamp: Long? = 0L,
    val attachedFiles: List<AttachFile>? = emptyList(),
) : MessageAbstract {
    override fun getRichText(): String {
        return content ?: ""
    }

    override fun getCopyableText(): String {
        return content ?: ""
    }

}

interface MessageAbstract {
    fun getRichText(): String?
    fun getCopyableText(): String?
}
