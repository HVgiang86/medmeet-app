package com.gianghv.kmachat.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gianghv.kmachat.component.ChatInput
import com.gianghv.kmachat.component.ChatMessage
import com.gianghv.kmachat.core.ChatContract
import com.gianghv.kmachat.core.ChatStore
import com.gianghv.kmachat.model.Message
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    store: ChatStore = remember { ChatStore() }
) {
    val state by store.state.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle effects
    LaunchedEffect(Unit) {
        store.effect.collectLatest { effect ->
            when (effect) {
                is ChatContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ChatContract.Effect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                ChatContract.Effect.ScrollToBottom -> {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KMA Chat") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
                    }
                }
            )
        },
        bottomBar = {
            ChatInput(
                value = state.inputText,
                onValueChange = { store.handleAction(ChatContract.Action.UpdateInputText(it)) },
                onSend = { store.handleAction(ChatContract.Action.SendMessage(state.inputText)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = true
        ) {
            if (state.isGenerating) {
                item {
                    ChatMessage(
                        message = "Generating response...",
                        isFromUser = false,
                        isGenerating = true
                    )
                }
            }
            
            items(
                items = state.messages.asReversed(),
                key = { it.id }
            ) { message ->
                ChatMessage(
                    message = message.content,
                    isFromUser = message.isFromUser
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val previewMessages = listOf(
        Message("1", "Hello! How can I help you today?", false),
        Message("2", "I have a question about programming", true),
        Message("3", "Sure, I'd be happy to help! What would you like to know?", false)
    )
    
    val previewStore = ChatStore().apply {
        handleAction(ChatContract.Action.SendMessage("I have a question about programming"))
    }
    
    ChatScreen(
        onOpenDrawer = {},
        store = previewStore
    )
}

@Preview(showBackground = true)
@Composable
fun ChatMessagePreview() {
    Column {
        ChatMessage(
            message = "Hello! How can I help you today?",
            isFromUser = false
        )
        ChatMessage(
            message = "I have a question about programming",
            isFromUser = true
        )
        ChatMessage(
            message = "Generating response...",
            isFromUser = false,
            isGenerating = true
        )
    }
}

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean
) 