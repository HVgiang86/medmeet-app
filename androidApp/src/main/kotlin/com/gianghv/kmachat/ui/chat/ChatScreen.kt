package com.gianghv.kmachat.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.Toaster
import com.gianghv.kmachat.R
import com.gianghv.kmachat.component.BaseInputText
import com.gianghv.kmachat.component.BotMessage
import com.gianghv.kmachat.component.ChatInputSection
import com.gianghv.kmachat.component.FailDialog
import com.gianghv.kmachat.component.HumanChatMessage
import com.gianghv.kmachat.component.WavingDots
import com.gianghv.kmachat.shared.app.chat.ChatAction
import com.gianghv.kmachat.shared.app.chat.ChatEffect
import com.gianghv.kmachat.shared.app.chat.ChatStore
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pullRefresh(refreshState)
    ) {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) {
            Icon(imageVector = Icons.Default.Menu,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onOpenDrawer()
                    })

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Chat",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "More",
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_new_chat),
                contentDescription = "New chat",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {

                    })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                val itemNumber = if (state.isGenerating) {
                    state.messages.size + 1
                } else {
                    state.messages.size
                }

                items(itemNumber) { index ->
                    if (index == state.messages.size) {
                        // Show generating indicator
                        WavingDots()
                        return@items
                    }

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

            Spacer(modifier = Modifier.height(16.dp))

            ChatInputSection(modifier = Modifier.fillMaxWidth(), onMessageSent = { text ->
                store.sendAction(ChatAction.SendMessage(text))

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
            scale = true
        )
    }
}


