package com.huongmt.medmeet.ui.profile.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.LanguageSelectionDialog
import com.huongmt.medmeet.shared.app.LanguageAction
import com.huongmt.medmeet.shared.app.LanguageStore
import com.huongmt.medmeet.shared.app.ProfileState
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingBottomSheet(
    appState: ProfileState,
    languageStore: LanguageStore,
    prefsStorage: PrefsStorage,
    state: ModalBottomSheetState? = null,
    onChangeBackendServer: (String) -> Unit = {},
    onChangeChatServer: (String) -> Unit,
) {
    val show = state ?: rememberModalBottomSheetState(
        initialDetent = FullyExpanded, detents = listOf(Hidden, FullyExpanded)
    )

    val scope = rememberCoroutineScope()
    
    // Add language store and PrefsStorage
    val languageState by languageStore.observeState().collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = languageState.currentLanguage,
            availableLanguages = languageState.availableLanguages,
            onLanguageSelected = { language ->
                languageStore.sendAction(LanguageAction.ChangeLanguage(language))
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    ModalBottomSheet(state = show) {
        Sheet(
            modifier = Modifier
                .padding(top = 12.dp)
                .shadow(8.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
            ) {
                DragIndication(
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Color.Black.copy(0.4f), RoundedCornerShape(100)
                        )
                        .width(32.dp)
                        .height(4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Cài đặt",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Language setting item
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ngôn ngữ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                        Text(
                            text = languageState.currentLanguage.nativeName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "URL Máy chủ Backend",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                BaseInputText(
                    default = appState.currentBackendServer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(70.dp),
                    hint = "URL Máy chủ Backend",
                    description = "URL Máy chủ Backend",
                    onTextChanged = {
                        onChangeBackendServer(it)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onImeAction = {
                        onChangeBackendServer(it)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "URL Máy chủ AI Chat",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                BaseInputText(
                    default = appState.currentChatServer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(70.dp),
                    hint = "URL Máy chủ AI Chat",
                    description = "URL Máy chủ AI Chat",
                    onTextChanged = {
                        onChangeChatServer(it)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onImeAction = {
                        onChangeChatServer(it)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
