package com.huongmt.medmeet.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.shared.app.ChatAction
import com.huongmt.medmeet.shared.app.ChatStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    store: ChatStore,
) {
    val state by store.observeState().collectAsState()

    ModalDrawerSheet(
        modifier = modifier.fillMaxWidth(0.8f),
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            "Chat History",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
        )

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        if (state.conversationList.isEmpty()) {
            NavigationDrawerItem(
                label = { Text("New Chat") },
                selected = false,
                onClick = {
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(state.conversationList.size) { index ->
                val conversation = state.conversationList[index]
                NavigationDrawerItem(
                    label = { Text(conversation.title ?: "") },
                    selected = state.currentConversationId == conversation.id,
                    onClick = {
                        store.sendAction(
                            ChatAction.SelectConversation(conversation.id),
                        )
                        onClose()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    badge = {
                        IconButton(
                        onClick = {
                            store.sendAction(
                                ChatAction.DeleteConversation(conversation.id),
                            )
                        },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        }

        HorizontalDivider()

        PrimaryButton(
            text = {
                Text("New Chat")
            },
            onClick = {
                store.sendAction(ChatAction.NewConversation)
                onClose()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(vertical = 12.dp)
        )
    }
} 
