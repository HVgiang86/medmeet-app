package com.gianghv.kmachat.core

import com.gianghv.kmachat.model.Message
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockChatApi {
    private val responses = listOf(
        "I understand your question. Let me help you with that.",
        "That's an interesting point. Here's what I think...",
        "Based on my knowledge, I can explain that...",
        "Let me provide some information about that...",
        "I can help you understand this better..."
    )

    suspend fun generateResponse(userMessage: String): Message {
        delay(1500) // Simulate network delay
        return Message(
            id = Random.nextInt().toString(),
            content = responses.random(),
            isFromUser = false
        )
    }
} 