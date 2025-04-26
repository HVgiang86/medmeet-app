package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.number

interface ChatRepository {
    suspend fun sendMessage(
        message: String,
        conversationId: String
    ): Flow<Message>

    suspend fun getMessageHistory(conversationId: String): Flow<List<Message>>

    suspend fun getConversationList(): Flow<List<Conversation>>

    suspend fun createConversation(): Flow<Conversation>

    suspend fun deleteConversation(conversationId: String): Flow<Boolean>
}

class ChatRepositoryImpl(
    private val api: APIs
) : BaseRepository(), ChatRepository {
    override suspend fun sendMessage(
        message: String,
        conversationId: String
    ): Flow<Message> = flowContext(mapper = {
        it.toMessage(userId = WholeApp.USER_ID, conversationId = conversationId)
    }) {
        api.queryAiMessage(conversationId = conversationId, message = message)
    }

    override suspend fun getMessageHistory(conversationId: String): Flow<List<Message>> =
        flowContext(mapper = {
            it.map { msgResponse ->
                msgResponse.toMessage(userId = WholeApp.USER_ID, conversationId = conversationId)
            }
        }) {
            api.getMessagesOfConversation(conversationId = conversationId)
        }

    override suspend fun getConversationList(): Flow<List<Conversation>> = flowContext(mapper = {
        it.map { conversationRsp ->
            conversationRsp.toConversation()
        }
    }) {
        api.getConversations(uid = WholeApp.USER_ID)
    }

    override suspend fun createConversation(): Flow<Conversation> = flowContext(mapper = {
        it.toConversation()
    }) {
        Napier.d { "Create from repository" }
        val now = nowDateTime()
        val dateStr =
            "Chat - ${now.hour}:${now.minute}:${now.second} ${now.dayOfMonth}/${now.month.number}/${now.year}"
        api.createConversation(uid = WholeApp.USER_ID, title = dateStr)
    }

    override suspend fun deleteConversation(conversationId: String): Flow<Boolean> =
        returnIfSuccess {
            api.deleteConversation(conversationId = conversationId)
        }
}
