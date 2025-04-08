package com.gianghv.kmachat.core

import com.gianghv.kmachat.model.Message

object ChatContract {
    data class State(
        val messages: List<Message> = emptyList(),
        val isGenerating: Boolean = false,
        val inputText: String = "",
        val error: String? = null
    )

    sealed interface Action {
        data class UpdateInputText(val text: String) : Action
        data class SendMessage(val text: String) : Action
        object ClearError : Action
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class ShowToast(val message: String) : Effect
        object ScrollToBottom : Effect
    }
} 