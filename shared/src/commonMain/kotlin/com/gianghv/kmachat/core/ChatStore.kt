package com.gianghv.kmachat.core

import com.gianghv.kmachat.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatStore(
    private val chatApi: MockChatApi = MockChatApi(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _state = MutableStateFlow(ChatContract.State())
    val state: StateFlow<ChatContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChatContract.Effect>()
    val effect: SharedFlow<ChatContract.Effect> = _effect.asSharedFlow()

    fun handleAction(action: ChatContract.Action) {
        when (action) {
            is ChatContract.Action.UpdateInputText -> {
                _state.value = _state.value.copy(inputText = action.text)
            }
            is ChatContract.Action.SendMessage -> {
                if (action.text.isBlank()) return
                sendMessage(action.text)
            }
            is ChatContract.Action.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun sendMessage(text: String) {
        val userMessage = Message(
            id = Random.nextInt().toString(),
            content = text,
            isFromUser = true
        )

        scope.launch {
            // Add user message
            _state.value = _state.value.copy(
                messages = _state.value.messages + userMessage,
                inputText = "",
                isGenerating = true
            )
            _effect.emit(ChatContract.Effect.ScrollToBottom)

            try {
                // Get AI response
                val aiResponse = chatApi.generateResponse(text)
                _state.value = _state.value.copy(
                    messages = _state.value.messages + aiResponse,
                    isGenerating = false
                )
                _effect.emit(ChatContract.Effect.ScrollToBottom)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isGenerating = false,
                    error = e.message
                )
                _effect.emit(ChatContract.Effect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
} 