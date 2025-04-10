package com.gianghv.kmachat.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gianghv.kmachat.component.BotMessage
import com.gianghv.kmachat.component.FailDialog
import com.gianghv.kmachat.component.HumanChatMessage
import com.gianghv.kmachat.shared.app.chat.ChatAction
import com.gianghv.kmachat.shared.app.chat.ChatEffect
import com.gianghv.kmachat.shared.app.chat.ChatStore
import com.gianghv.kmachat.shared.core.entity.Message
import com.gianghv.kmachat.utils.Logger
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatScreen(
    private val onOpenDrawer: () -> Unit,
) : Screen, KoinComponent {

    @Composable
    override fun Content() {
        val store: ChatStore by inject()
        Napier.d("ChatScreen")
        ChatScreenContent(
            onOpenDrawer = onOpenDrawer, store = store
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Screen.ChatScreenContent(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    store: ChatStore,
) {
    val state by store.observeState().collectAsState()
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow

    val refreshState = rememberPullRefreshState(refreshing = state.isLoading, onRefresh = {
        store.sendAction(ChatAction.GetMessageHistory)
    })

    val errorEffect by store.observeSideEffect().filterIsInstance<ChatEffect.ShowError>()
        .collectAsState(null)

    val toastEffect by store.observeSideEffect().filterIsInstance<ChatEffect.ShowToast>()
        .collectAsState(null)

    // Handle effects
    LaunchedEffect(Unit) {
        store.sendAction(ChatAction.GetMessageHistory)
    }

    // Show error message
    errorEffect?.let {
        FailDialog(
            title = "Error", content = it.message
        )
    }

    Box(modifier = Modifier.pullRefresh(refreshState)) {
        LazyColumn {
            items(state.messages.size) { index ->
                val message = state.messages[index]

                if (message.isHuman) {
                    HumanChatMessage(
                        message = message
                    )
                } else {
                    BotMessage(
                        message = message
                    )
                }
            }
        }

        PullRefreshIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            refreshing = state.isLoading,
            state = refreshState,
            scale = true
        )
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

}


