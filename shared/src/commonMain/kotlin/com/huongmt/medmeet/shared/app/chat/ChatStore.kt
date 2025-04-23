package com.huongmt.medmeet.shared.app.chat

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import com.huongmt.medmeet.shared.core.repository.ChatRepository
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

data class ChatState(
    val messages: List<Message> = emptyList(),
    val conversationList: List<Conversation> = emptyList(),
    val displayConversationId: String? = null,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false
) : Store.State(loading = isLoading) {
    val isEmpty: Boolean
        get() = messages.isEmpty()
}

sealed interface ChatAction : Store.Action {
    data class SendMessage(
        val text: String,
        val conversationId: String? = null
    ) : ChatAction

    data class NewConversation(
        val text: String
    ) : ChatAction

    data class GetConversationList(
        val showLatestConversation: Boolean = false
    ) : ChatAction

    data class SelectConversation(
        val conversationId: String
    ) : ChatAction

    data class GetConversationListSuccess(
        val conversations: List<Conversation>,
        val showLatestConversation: Boolean = false
    ) : ChatAction

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
        val error: String
    ) : ChatAction
}

sealed interface ChatEffect : Store.Effect {
    data class ShowError(
        val message: String
    ) : ChatEffect

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
            Napier.e(tag = "ChatStore", message = "Exception: $it")
            setEffect(ChatEffect.ShowError(it.message ?: "Unknown error"))
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
                    getMessageHistory(conversationId = action.conversationId)
                }
            }

            is ChatAction.GetMessageHistorySuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        messages = action.messages,
                        isGenerating = false,
                        displayConversationId = action.conversationId
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
                setEffect(ChatEffect.ShowError(action.error))
                setState(
                    oldState.copy(
                        isLoading = false,
                        isGenerating = false
                    )
                )
            }

            is ChatAction.GetConversationList -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                    getConversationList(action.showLatestConversation)
                }
            }

            is ChatAction.GetConversationListSuccess -> {
                if (action.showLatestConversation) {
                    setState(
                        oldState.copy(
                            isLoading = true,
                            conversationList = action.conversations
                        )
                    )
                    action.conversations.firstOrNull()?.let { conversation ->
                        getMessageHistory(conversation.id)
                    }
                } else {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            conversationList = action.conversations
                        )
                    )
                }
            }

            is ChatAction.NewConversation -> {
            }

            is ChatAction.NewConversationSuccess -> {
            }

            is ChatAction.SelectConversation -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                    getMessageHistory(action.conversationId)
                }
            }
        }
    }

    private fun getConversationList(showLatestConversation: Boolean) {
        launch {
            chatRepository.getConversationList().collect { conversations ->
                sendAction(
                    ChatAction.GetConversationListSuccess(
                        conversations,
                        showLatestConversation
                    )
                )
            }
        }
    }

    private fun getMessageHistory(conversationId: String) {
        launch {
            chatRepository.getMessageHistory(conversationId).collect { messages ->
                sendAction(ChatAction.GetMessageHistorySuccess(messages, conversationId))
            }
        }
    }

    private fun sendMessage(
        text: String,
        conversationId: String
    ) {
        launch {
            chatRepository.sendMessage(text).collect { message ->
                sendAction(ChatAction.SendMessageSuccess(message, conversationId))
            }
        }
    }

    private fun generateHumanMessage(
        text: String,
        conversationId: String
    ): Message =
        Message(
            id =
            Clock.System
                .now()
                .toEpochMilliseconds()
                .toString(),
            userId = "human",
            content = text,
            isHuman = true,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            attachedFiles = emptyList(),
            conversationId = conversationId // Replace with actual conversation ID if needed
        )
}
