package com.huongmt.medmeet.ui.chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BotMessage
import com.huongmt.medmeet.component.ChatInputSection
import com.huongmt.medmeet.component.DrawerMenu
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.HorizontalMedicalServiceCard
import com.huongmt.medmeet.component.HumanChatMessage
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.component.MedicalServiceBottomSheet
import com.huongmt.medmeet.component.RecommendedQueriesOverlay
import com.huongmt.medmeet.component.WavingDots
import com.huongmt.medmeet.data.WholeApp
import com.huongmt.medmeet.shared.app.ChatAction
import com.huongmt.medmeet.shared.app.ChatEffect
import com.huongmt.medmeet.shared.app.ChatStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ChatScreenContent(chatStore: ChatStore, onBack: () -> Unit = {}) {
    val scope = rememberCoroutineScope()

    BackHandler(enabled = true, onBack = {
        onBack()
    })

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            DrawerMenu(onClose = {
                scope.launch {
                    drawerState.close()
                }
            }, store = chatStore)
        },
        drawerState = drawerState,
    ) {
        Box(
            modifier = Modifier.statusBarsPadding(),
        ) {
            ChatScreenContentT(
                onOpenDrawer = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) {
                                Napier.d { "Open drawer" }
                                open()
                            } else {
                                Napier.d { "Close drawer" }
                                close()
                            }
                        }
                    }
                },
                store = chatStore,
                onBack = onBack,
            )

        }
    }

}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun ChatScreenContentT(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    store: ChatStore,
    onBack: () -> Unit = {},
) {
    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(null)

    val context = LocalContext.current
    var spokenText by remember { mutableStateOf("Press the button and start speaking") }
    var isListening by remember { mutableStateOf(false) }

    val inputState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val listState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(refreshing = state.isLoading, onRefresh = {
        val conversationId = state.currentConversationId
        if (conversationId != null) {
            store.sendAction(ChatAction.GetMessageHistory(conversationId))
        }
    })

    val toastEffect by store.observeSideEffect().filterIsInstance<ChatEffect.ShowToast>()
        .collectAsState(null)

    // Handle effects
    LaunchedEffect(Unit) {
        if (!WholeApp.confirmChatBot) {
            store.sendAction(ChatAction.GetConversationList(showLatest = true))
        } else {
            store.sendAction(ChatAction.GetConversationList(showLatest = false))
            store.sendAction(ChatAction.NewConversation)
        }
    }

    // Scroll to bottom when messages change or when generating starts/stops
    LaunchedEffect(state.messages.size, state.isGenerating) {
        if (state.messages.isNotEmpty() || state.isGenerating) {
            listState.animateScrollToItem(
                if (state.isGenerating) state.messages.size else state.messages.size - 1,
            )
        }
    }

    LaunchedEffect(effect) {
        when (effect) {
            is ChatEffect.ShowToast -> {
                val message = (effect as ChatEffect.ShowToast).message
                Napier.d { "Show toast: $message" }
                // Show toast using Android's Toast or any other method
                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            is ChatEffect.ScrollToBottom -> {
                // Scroll to bottom when this effect is triggered
                if (state.messages.isNotEmpty()) {
                    listState.animateScrollToItem(state.messages.size - 1)
                }
            }
            else -> {
                // Handle other effects if needed
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    if (state.error != null) {
        ErrorDialog(throwable = state.error, onDismissRequest = {
            store.sendAction(ChatAction.ClearError)
        })
    }

    // Medical Service Bottom Sheet
    if (state.showMedicalServiceBottomSheet && state.selectedMedicalService != null) {
        MedicalServiceBottomSheet(
            service = state.selectedMedicalService!!,
            clinic = state.medicalServiceClinic,
            onDismiss = {
                store.sendAction(ChatAction.HideMedicalServiceBottomSheet)
            },
            onBook = {
                store.sendAction(ChatAction.HideMedicalServiceBottomSheet)
                // onNavigateToBooking(state.selectedMedicalService)
            }
        )
    }

    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            spokenText = results?.getOrNull(0) ?: "No speech detected"
            Napier.d { "Speech recognition result: $spokenText" }
            inputState.value = TextFieldValue(spokenText)
        } else {
            // Handle errors or cancellation
            spokenText = "Nhận dạng thất bại hoặc bị hủy"
        }
        isListening = false // Reset listening state
    }

    // --- ActivityResultLauncher for Permission Request ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, launch the speech recognizer
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                ) // Use device's default language
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...") // Prompt shown to user
            }
            try {
                speechRecognitionLauncher.launch(intent)
                isListening = true // Update listening state
            } catch (e: Exception) {
                // Handle exceptions, e.g., recognizer not available
                spokenText = "Lỗi khởi chạy công cụ nhận dạng: ${e.message}"
                isListening = false
            }
        } else {
            // Permission denied
            spokenText = "Quyền ghi âm bị từ chối"
            isListening = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pullRefresh(refreshState),
    ) {

        TopAppBar(
            title = { Text(text = "Trợ lý y tế MedMeet") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    onOpenDrawer()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_chat_history),
                        contentDescription = null,
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth(),
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
//                        if (!state.isGenerating) {
//                            // Show medical services after the last AI message (if there are messages and last message is from AI)
//                            if (state.messages.isNotEmpty() && !state.messages.last().isHuman && state.medicalServices.isNotEmpty()) {
//                                Column {
//                                    Spacer(modifier = Modifier.height(16.dp))
//
//                                    Text(
//                                        text = "Dịch vụ y tế được đề xuất",
//                                        style = MaterialTheme.typography.titleMedium,
//                                        fontWeight = FontWeight.Medium,
//                                        color = MaterialTheme.colorScheme.onSurface,
//                                        modifier = Modifier.padding(vertical = 8.dp)
//                                    )
//
//                                    LazyRow(
//                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
//                                        contentPadding = PaddingValues(horizontal = 4.dp)
//                                    ) {
//                                        items(state.medicalServices) { service ->
//                                            HorizontalMedicalServiceCard(
//                                                service = service,
//                                                onClick = {
//                                                    store.sendAction(ChatAction.SelectMedicalService(service))
//                                                }
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//                            return@items
//                        }

                        if (!state.isGenerating)
                            return@items

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

            // Recommended queries overlay
            RecommendedQueriesOverlay(
                queries = state.recommendedQueries,
                visible = state.isGenQueriesEnabled && inputState.value.text.isEmpty() && !state.isGenerating,
                onQueryClick = { query ->
                    inputState.value = TextFieldValue(query)
                },
                modifier = Modifier
                    .wrapContentHeight().padding(bottom = 16.dp).padding(horizontal = 16.dp)
            )

            ChatInputSection(
                genQueriesEnable = state.isGenQueriesEnabled,
                textState = inputState,
                modifier = Modifier.fillMaxWidth(),
                onMessageSent = { text ->
                    Napier.d("Send message: $text")
                    store.sendAction(ChatAction.SendMessage(text, state.currentConversationId))
                },
//                onExpandRequest = {
//                    onOpenDrawer()
//                },
                onGenQueriesEnable = { enabled ->
                    store.sendAction(ChatAction.ToggleGenQueries(enabled))
                    Napier.d { "Toggle Gen Queries: $enabled" }
                },
                onMicrophoneClick = {
                    // Check for permission before launching
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ),
                        -> {
                            // Permission already granted, launch recognizer directly
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                            }
                            try {
                                speechRecognitionLauncher.launch(intent)
                                isListening = true
                            } catch (e: Exception) {
                                spokenText = "Lỗi khởi chạy công cụ nhận dạng: ${e.message}"
                                isListening = false
                            }
                        }

                        else -> {
                            // Request permission
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                })
        }


    }
}
