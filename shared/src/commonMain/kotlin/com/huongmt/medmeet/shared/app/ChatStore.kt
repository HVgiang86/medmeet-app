package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import com.huongmt.medmeet.shared.core.repository.ChatRepository
import com.huongmt.medmeet.shared.utils.ext.nowDateTime

data class ChatState(
    val messages: List<Message> = emptyList(),
    val conversationList: List<Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null
) : Store.State(loading = isLoading)

sealed interface ChatAction : Store.Action {
    data class SendMessage(
        val text: String,
        val conversationId: String? = null
    ) : ChatAction

    data object NewConversation : ChatAction

    data class GetConversationList(val showLatest: Boolean = false) : ChatAction

    data class SelectConversation(
        val conversationId: String
    ) : ChatAction

    data class GetConversationListSuccess(
        val conversations: List<Conversation>,
        val showLatest: Boolean
    ) : ChatAction

    data object GetConversationListError : ChatAction

    data class GetMessageHistory(
        val conversationId: String
    ) : ChatAction

    data class SendMessageSuccess(
        val message: Message,
        val conversationId: String? = null
    ) : ChatAction

    data class GetMessageHistorySuccess(
        val messages: List<Message>,
        val conversationId: String
    ) : ChatAction

    data class NewConversationSuccess(
        val conversation: Conversation
    ) : ChatAction

    data class Error(
        val error: Throwable
    ) : ChatAction

    data class DeleteConversation(
        val conversationId: String
    ) : ChatAction

    data class DeleteConversationSuccess(
        val conversationId: String
    ) : ChatAction

    data object ClearError : ChatAction
}

sealed interface ChatEffect : Store.Effect {
    data class ShowToast(
        val message: String
    ) : ChatEffect

    data object ScrollToBottom : ChatEffect
}

class ChatStore(
    private val chatRepository: ChatRepository
) : Store<ChatState, ChatAction, ChatEffect>(
    ChatState(
        isGenerating = false,
        messages = emptyList(),
        isLoading = false
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(
                ChatAction.Error(it)
            )
        }

    override fun dispatch(
        oldState: ChatState,
        action: ChatAction
    ) {
        when (action) {
            is ChatAction.SendMessage -> {
                if (action.conversationId == null) {
                    return
                }

                if (!oldState.isLoading && !oldState.isGenerating) {
                    val message =
                        generateHumanMessage(action.text, conversationId = action.conversationId)

                    setState(
                        oldState.copy(
                            messages = oldState.messages + message,
                            isGenerating = true
                        )
                    )

                    sendMessage(action.text, conversationId = action.conversationId)
                }
            }

            is ChatAction.GetMessageHistory -> {
                if (!oldState.isLoading) {
                    setState(
                        oldState.copy(isLoading = true, messages = emptyList())
                    )
                }
                getMessageHistory(conversationId = action.conversationId)
            }

            is ChatAction.GetMessageHistorySuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        messages = action.messages,
                        isGenerating = false,
                        currentConversationId = action.conversationId
                    )
                )
            }

            is ChatAction.SendMessageSuccess -> {
                val messages = oldState.messages + action.message
                setEffect(ChatEffect.ScrollToBottom)
                setState(
                    oldState.copy(
                        messages = messages,
                        isLoading = false,
                        isGenerating = false
                    )
                )
            }

            is ChatAction.Error -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        isGenerating = false,
                        error = action.error
                    )
                )
            }

            is ChatAction.GetConversationList -> {
                getConversationList(action.showLatest)
            }

            is ChatAction.GetConversationListSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        isGenerating = false,
                        conversationList = action.conversations
                    )
                )
                if (action.showLatest) {
                    sendAction(ChatAction.SelectConversation(action.conversations.first().id))
                }
            }

            is ChatAction.NewConversation -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                }
                createConversation()
            }

            is ChatAction.NewConversationSuccess -> {
                val conversations = oldState.conversationList + action.conversation
                val sorted = conversations.sortedByDescending { it.updatedAt }
                setState(
                    oldState.copy(
                        isLoading = false,
                        messages = emptyList(),
                        currentConversationId = action.conversation.id,
                        conversationList = sorted
                    )
                )
                sendAction(ChatAction.SelectConversation(action.conversation.id))
            }

            is ChatAction.SelectConversation -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                    getMessageHistory(action.conversationId)
                }
            }

            ChatAction.ClearError -> setState(oldState.copy(error = null))
            ChatAction.GetConversationListError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = null,
                        conversationList = emptyList()
                    )
                )
            }

            is ChatAction.DeleteConversation -> {
                deleteConversation(action.conversationId)
            }

            is ChatAction.DeleteConversationSuccess -> {
                val conversations = oldState.conversationList.filter {
                    it.id != action.conversationId
                }.sortedByDescending { it.updatedAt }

                setState(
                    oldState.copy(
                        isLoading = false,
                        conversationList = conversations
                    )
                )
            }
        }
    }

    private fun deleteConversation(
        conversationId: String
    ) {
        runFlow(
            exception = coroutineExceptionHandler {
            }
        ) {
            chatRepository.deleteConversation(conversationId).collect { success ->
                if (success) {
                    sendAction(ChatAction.DeleteConversationSuccess(conversationId))
                }
            }
        }
    }

    private fun generateHumanMessage(
        text: String,
        conversationId: String
    ): Message {
        val nowDate = nowDateTime()
        return Message(
            id = "$nowDate",
            userId = "",
            content = text,
            conversationId = conversationId,
            isHuman = true,
            timestamp = nowDate
        )
    }

    private fun createConversation() {
        runFlow {
            chatRepository.createConversation().collect { conversation ->
                sendAction(ChatAction.NewConversationSuccess(conversation))
            }
        }
    }

    private fun getConversationList(showLatest: Boolean) {
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(ChatAction.GetConversationListError)
            }
        ) {
            chatRepository.getConversationList().collect { conversations ->
                val sorted = conversations.sortedByDescending { it.updatedAt }
                sendAction(
                    ChatAction.GetConversationListSuccess(sorted, showLatest)
                )
            }
        }
    }

    private fun getMessageHistory(conversationId: String) {
        runFlow {
            chatRepository.getMessageHistory(conversationId).collect { messages ->
                sendAction(ChatAction.GetMessageHistorySuccess(messages, conversationId))
            }
        }
    }

    private fun sendMessage(
        text: String,
        conversationId: String
    ) {
        runFlow {
            chatRepository.sendMessage(message = text, conversationId = conversationId)
                .collect { message ->
                    sendAction(ChatAction.SendMessageSuccess(message, conversationId))
                }
        }
    }
}
