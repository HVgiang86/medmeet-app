package com.gianghv.kmachat.shared.core.repository

import com.gianghv.kmachat.shared.base.BaseRepository
import com.gianghv.kmachat.shared.core.datasource.network.MockChatApi
import com.gianghv.kmachat.shared.core.entity.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // Define your repository methods here
    suspend fun sendMessage(message: String): Flow<Message>
    suspend fun getMessageHistory(): Flow<List<Message>>
}

class ChatRepositoryImpl(
    private val chatApi: MockChatApi,
) : BaseRepository(), ChatRepository {
    override suspend fun sendMessage(message: String): Flow<Message> = flowContext {
        chatApi.generateResponse(message)
    }

    override suspend fun getMessageHistory(): Flow<List<Message>> = flowContext {
        chatApi.getResponseList()
    }
}