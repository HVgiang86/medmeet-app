package com.gianghv.kmachat.shared.core.datasource.network

import com.gianghv.kmachat.shared.base.BaseResponse
import com.gianghv.kmachat.shared.core.entity.Message
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.random.Random
val mockMsg = Message(
    id = "1",
    userId = "userId",
    content = """
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

class MockChatApi(httpClient: HttpClient) {
    // Generate sample response in markdown format
    private val responseMarkdown = """
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

    private val responses = mutableListOf(
        "I understand your question. Let me help you with that.",
        "That's an interesting point. Here's what I think...",
        "Based on my knowledge, I can explain that...",
        "Let me provide some information about that...",
        "I can help you understand this better...",
        responseMarkdown
    )


    suspend fun generateResponse(userMessage: String): BaseResponse<Message> {
        delay(1500) // Simulate network delay
        val userIdSeed = "userId"
        // Generate a random user ID
        val userId = userIdSeed + Random.nextInt(1, 1000).toString()
        val msg = Message(
            id = Random.nextInt().toString(),
            userId = userId,
            content = responses.random(),
            isHuman = false,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            attachedFiles = emptyList()
        )
        return BaseResponse(
            code = 200, message = "OK", data = msg
        )
    }

    suspend fun getResponseList(): BaseResponse<List<Message>> {
        delay(1500) // Simulate network delay
        val userIdSeed = "userId"

        val msgList = mutableListOf<Message>()

        for (i in 0..5) {
            // Generate a random user ID
            val userId = userIdSeed + Random.nextInt(1, 1000).toString()
            val msg = Message(
                id = (Random.nextInt() + i).toString(),
                userId = userId,
                content = responses.random(),
                isHuman = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                attachedFiles = emptyList()
            )
            msgList.add(msg)
        }
        return BaseResponse(
            code = 200, message = "OK", data = msgList
        )
    }
}
