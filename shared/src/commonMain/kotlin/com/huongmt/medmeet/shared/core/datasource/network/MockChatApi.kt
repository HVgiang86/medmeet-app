package com.huongmt.medmeet.shared.core.datasource.network

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.random.Random

val mockMsg =
    Message(
        id = "1",
        userId = "userId",
        conversationId = "1",
        content =
        """
            # Sample Response
            This is a sample response in markdown format.
            
            [Link](https://example.com)
            ## Quote
            > This is a quote.
            ## Math 
            $$\E = mc^2$$
            ## HTML
            <div>
                <p>This is a paragraph in HTML.</p>
            </div>
        """.trimIndent(),
        isHuman = true,
        timestamp = Clock.System.now().toEpochMilliseconds(),
        attachedFiles = emptyList()
    )

class MockChatApi(
    httpClient: HttpClient
) {
    // Generate sample response in markdown format
    private val responseMarkdown =
        """
        # Sample Response
        This is a sample response in markdown format.
        
        ## Subheading
        - Item 1
        - Item 2
        - Item 3
        
        **Bold Text**
        
        *Italic Text*
        
        [Link](https://example.com)
        
        ```kotlin
        fun main() {
            println("Hello, World!")
        }
        ```
        > This is a blockquote.
        ![Image](https://example.com/image.png)
        ```
        {
            "key": "value"
        }
        ```
        ## Code Block
        ```python
        def hello_world():
            print("Hello, World!")
        ```
        ## List
        - Item 1
        - Item 2
        - Item 3
        -- subitem 1
        -- subitem 2
        ## Table
        | Header 1 | Header 2 |
        |----------|----------|
        | Row 1    | Row 2    |
        | Row 3    | Row 4    |
        ## Quote
        > This is a quote.
        ## Math 
        $$\E = mc^2$$
        ## HTML
        <div>
            <p>This is a paragraph in HTML.</p>
        </div>
        """.trimIndent()

    private val responses =
        mutableListOf(
            "I understand your question. Let me help you with that.",
            "That's an interesting point. Here's what I think...",
            "Based on my knowledge, I can explain that...",
            "Let me provide some information about that...",
            "I can help you understand this better...",
            responseMarkdown
        )

    private val conversationList =
        mutableListOf(
            Conversation(
                id = "1",
                name = "Conversation 1",
                description = "This is the first conversation.",
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            ),
            Conversation(
                id = "2",
                name = "Conversation 2",
                description = "This is the second conversation.",
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            ),
            Conversation(
                id = "3",
                name = "Conversation 3",
                description = "This is the third conversation.",
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
        )

    suspend fun generateResponse(
        userMessage: String,
        conversationId: String? = null
    ): BaseResponse<Message> {
        delay(1500) // Simulate network delay
        val userIdSeed = "userId"
        // Generate a random user ID
        val userId = userIdSeed + Random.nextInt(1, 1000).toString()
        val msg =
            Message(
                id = Random.nextInt().toString(),
                userId = userId,
                content = responses.random(),
                isHuman = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                attachedFiles = emptyList(),
                conversationId = conversationId ?: conversationList.random().id
            )
        return BaseResponse(
            code = 200,
            message = "OK",
            data = msg
        )
    }

    suspend fun getResponseList(conversationId: String): BaseResponse<List<Message>> {
        delay(1500) // Simulate network delay
        val userIdSeed = "userId"

        val msgList = mutableListOf<Message>()

        for (i in 0..5) {
            // Generate a random user ID
            val userId = userIdSeed + Random.nextInt(1, 1000).toString()
            val msg =
                Message(
                    id = (Random.nextInt() + i).toString(),
                    userId = userId,
                    content = responses.random(),
                    isHuman = false,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    attachedFiles = emptyList(),
                    conversationId = conversationId
                )
            msgList.add(msg)
        }
        return BaseResponse(
            code = 200,
            message = "OK",
            data = msgList
        )
    }

    suspend fun getConversationList(): BaseResponse<List<Conversation>> {
        delay(1500) // Simulate network delay
        return BaseResponse(
            code = 200,
            message = "OK",
            data = conversationList
        )
    }

    suspend fun createConversation(firstMsg: String): BaseResponse<Message> {
        delay(1500) // Simulate network delay
        val userIdSeed = "userId"
        // Generate a random user ID
        val userId = userIdSeed + Random.nextInt(1, 1000).toString()
        val newConversation =
            Conversation(
                id = Random.nextInt().toString(),
                name = "Conversation ${conversationList.size + 1}",
                description = "This is a new conversation.",
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
        conversationList.add(newConversation)

        val msg =
            Message(
                id = Random.nextInt().toString(),
                userId = userId,
                content = firstMsg,
                isHuman = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                attachedFiles = emptyList(),
                conversationId = newConversation.id
            )
        return BaseResponse(
            code = 200,
            message = "OK",
            data = msg
        )
    }
}
