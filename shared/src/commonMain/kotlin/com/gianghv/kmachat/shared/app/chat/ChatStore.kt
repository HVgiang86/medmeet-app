package com.gianghv.kmachat.shared.app.chat

import com.gianghv.kmachat.shared.base.Store
import com.gianghv.kmachat.shared.core.entity.Message
import com.gianghv.kmachat.shared.core.repository.ChatRepository
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false,
) : Store.State(loading = isLoading) {
    val isEmpty: Boolean
        get() = messages.isEmpty()
}

sealed interface ChatAction : Store.Action {
    data class SendMessage(val text: String) : ChatAction
    data object GetMessageHistory : ChatAction
    data class SendMessageSuccess(val message: Message) : ChatAction
    data class GetMessageHistorySuccess(val messages: List<Message>) : ChatAction
    data class Error(val error: String) : ChatAction
}

sealed interface ChatEffect : Store.Effect {
    data class ShowError(val message: String) : ChatEffect
    data class ShowToast(val message: String) : ChatEffect
    data object ScrollToBottom : ChatEffect
}

class ChatStore(
    private val chatRepository: ChatRepository,
) : Store<ChatState, ChatAction, ChatEffect>(
    ChatState(
        isGenerating = false, messages = emptyList(), isLoading = false
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            Napier.e(tag = "ChatStore", message = "Exception: $it")
            setEffect(ChatEffect.ShowError(it.message ?: "Unknown error"))
        }

    override fun dispatch(oldState: ChatState, action: ChatAction) {
        val newState = when (action) {
            is ChatAction.SendMessage -> {
                if (oldState.isLoading || oldState.isGenerating) {
                    oldState
                } else {
                    sendMessage(action.text)
                    val message = generateHumanMessage(action.text)
                    val messages = oldState.messages + message
                    oldState.copy(isGenerating = true, messages = messages)
                }
            }

            is ChatAction.GetMessageHistory -> {
                if (oldState.isLoading) {
                    oldState
                } else {
                    getMessageHistory()
                    oldState.copy(isLoading = true)
                }
            }

            is ChatAction.GetMessageHistorySuccess -> {
                oldState.copy(
                    isLoading = false, messages = action.messages, isGenerating = false
                )
            }

            is ChatAction.SendMessageSuccess -> {
                val messages = oldState.messages + action.message
                setEffect(ChatEffect.ScrollToBottom)
                oldState.copy(
                    isLoading = false, messages = messages, isGenerating = false
                )
            }

            is ChatAction.Error -> {
                setEffect(ChatEffect.ShowError(action.error))
                oldState.copy(isLoading = false, isGenerating = false)
            }
        }
        if (newState != oldState) {
            Napier.d(tag = "FeedStore", message = "NewState: $newState")
            setState(newState)
        }
    }

    private fun getMessageHistory() {
        launch {
            chatRepository.getMessageHistory().collect { messages ->
                sendAction(ChatAction.GetMessageHistorySuccess(messages))
            }
        }
    }

    private fun sendMessage(text: String) {
        launch {
            chatRepository.sendMessage(text).collect { message ->
                sendAction(ChatAction.SendMessageSuccess(message))
            }
        }
    }

    private fun generateHumanMessage(text: String): Message {
        return Message(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            userId = "human",
            content = text,
            isHuman = true,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            attachedFiles = emptyList(),
        )
    }
} 