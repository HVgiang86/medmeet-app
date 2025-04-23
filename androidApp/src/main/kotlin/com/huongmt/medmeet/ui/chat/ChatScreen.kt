package com.huongmt.medmeet.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BotMessage
import com.huongmt.medmeet.component.ChatInputSection
import com.huongmt.medmeet.component.FailDialog
import com.huongmt.medmeet.component.HumanChatMessage
import com.huongmt.medmeet.component.TopBar
import com.huongmt.medmeet.component.WavingDots
import com.huongmt.medmeet.shared.app.chat.ChatAction
import com.huongmt.medmeet.shared.app.chat.ChatEffect
import com.huongmt.medmeet.shared.app.chat.ChatStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(
    ExperimentalMaterialApi::class,
)
@Composable
fun Screen.ChatScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    store: ChatStore,
) {
    val state by store.observeState().collectAsState()
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val refreshState = rememberPullRefreshState(refreshing = state.isLoading, onRefresh = {
        val conversationId = state.displayConversationId
        if (conversationId != null) {
            store.sendAction(ChatAction.GetMessageHistory(conversationId))
        }
    })

    val errorEffect by store.observeSideEffect().filterIsInstance<ChatEffect.ShowError>()
        .collectAsState(null)

    val toastEffect by store.observeSideEffect().filterIsInstance<ChatEffect.ShowToast>()
        .collectAsState(null)

    // Create a derived state to track if the list is at the top
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        store.sendAction(ChatAction.GetConversationList(showLatestConversation = true))
    }

    // Scroll to bottom when messages change or when generating starts/stops
    LaunchedEffect(state.messages.size, state.isGenerating) {
        if (state.messages.isNotEmpty() || state.isGenerating) {
            listState.animateScrollToItem(
                if (state.isGenerating) state.messages.size else state.messages.size - 1,
            )
        }
    }

    // You can use this boolean in your composable or effects
    LaunchedEffect(isAtTop) { // Trigger recomposing when isAtTop changes
    }

    // Show error message
    errorEffect?.let {
        FailDialog(
            title = "Error",
            content = it.message,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pullRefresh(refreshState),
    ) {
        TopBar(
            onOpenDrawer = onOpenDrawer,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth(),
            title = stringResource(R.string.app_name),
            actionIcon = Icons.Default.Menu,
            isTop = isAtTop,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                val itemNumber = if (state.isGenerating) {
                    state.messages.size + 2
                } else {
                    state.messages.size + 1
                }

                items(itemNumber) { index ->
                    if (index == state.messages.size) {
                        if (!state.isGenerating) {
                            Spacer(modifier = Modifier.height(16.dp))
                            return@items
                        }

                        WavingDots()
                        return@items
                    }

                    if (index == state.messages.size + 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        return@items
                    }

                    val message = state.messages[index]

                    if (message.isHuman) {
                        HumanChatMessage(
                            message = message,
                        )
                    } else {
                        BotMessage(
                            message = message,
                        )
                    }
                }
            }

            ChatInputSection(modifier = Modifier.fillMaxWidth(), onMessageSent = { text ->
                Napier.d("Send message: $text")
                store.sendAction(ChatAction.SendMessage(text, state.displayConversationId))

                // Scroll to the bottom
            }, onExpandRequest = {
                onOpenDrawer()
            })
        }

        PullRefreshIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            refreshing = state.isLoading,
            state = refreshState,
            scale = true,
        )
    }
}
