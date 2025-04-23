package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.datasource.network.MockChatApi
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // Define your repository methods here
    suspend fun sendMessage(
        message: String,
        conversationId: String? = null
    ): Flow<Message>

    suspend fun getMessageHistory(conversationId: String): Flow<List<Message>>

    suspend fun getConversationList(): Flow<List<Conversation>>

    suspend fun createConversation(message: String): Flow<Message>
}

class ChatRepositoryImpl(
    private val chatApi: MockChatApi
) : BaseRepository(),
    ChatRepository {
    override suspend fun sendMessage(
        message: String,
        conversationId: String?
    ): Flow<Message> =
        flowContext {
            chatApi.generateResponse(message, conversationId)
        }

    override suspend fun getMessageHistory(conversationId: String): Flow<List<Message>> =
        flowContext {
            chatApi.getResponseList(conversationId)
        }

    override suspend fun getConversationList(): Flow<List<Conversation>> =
        flowContext {
            chatApi.getConversationList()
        }

    override suspend fun createConversation(message: String): Flow<Message> =
        flowContext {
            chatApi.createConversation(message)
        }
}
