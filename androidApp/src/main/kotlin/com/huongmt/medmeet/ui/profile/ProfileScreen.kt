package com.huongmt.medmeet.ui.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberDialogState
import com.composables.core.rememberModalBottomSheetState
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.ConfirmBottomSheet
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LanguageSelectionDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.LanguageAction
import com.huongmt.medmeet.shared.app.LanguageEffect
import com.huongmt.medmeet.shared.app.LanguageStore
import com.huongmt.medmeet.shared.app.ProfileAction
import com.huongmt.medmeet.shared.app.ProfileEffect
import com.huongmt.medmeet.shared.app.ProfileStore
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.theme.Grey_200
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination
import com.huongmt.medmeet.ui.profile.component.SettingBottomSheet
import com.huongmt.medmeet.utils.LanguageManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    store: ProfileStore,
    prefsStorage: PrefsStorage,
    languageStore: LanguageStore,
    navigateTo: (MainScreenDestination) -> Unit,
    onLogout: () -> Unit,
) {
    val state by store.observeState().collectAsState()
    val sideEffect by store.observeSideEffect().collectAsState(initial = null)
    
    // Add language store
    val languageState by languageStore.observeState().collectAsState()
    val languageEffect by languageStore.observeSideEffect().collectAsState(initial = null)

    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            ProfileEffect.Logout -> {
                onLogout()
            }

            ProfileEffect.NavigateExaminationHistory -> {

            }

            ProfileEffect.NavigateHealthRecord -> {
                navigateTo(MainScreenDestination.HealthRecord)
            }

            ProfileEffect.NavigateUpdateProfile -> {
                navigateTo(MainScreenDestination.ProfileDetail())
            }

            null -> {

            }
        }
    }
    
    LaunchedEffect(languageEffect) {
        when (languageEffect) {
            is LanguageEffect.LanguageChanged -> {
                // Handle language change - set locale and recreate activity
                android.util.Log.d("ProfileScreen", "Language effect received: ${(languageEffect as LanguageEffect.LanguageChanged).language.code} (${(languageEffect as LanguageEffect.LanguageChanged).language.nativeName})")
                LanguageManager.setLocale(context, (languageEffect as LanguageEffect.LanguageChanged).language)
                
                // Try different approaches for applying language change
                when {
                    context is androidx.activity.ComponentActivity -> {
                        android.util.Log.d("ProfileScreen", "Recreating activity for language change")
                        LanguageManager.recreateActivity(context)
                    }
                    context is android.app.Activity -> {
                        android.util.Log.d("ProfileScreen", "Recreating activity (fallback)")
                        LanguageManager.recreateActivity(context)
                    }
                    else -> {
                        android.util.Log.w("ProfileScreen", "Context is not Activity: ${context.javaClass.simpleName}")
                        // Force app restart as last resort
                        try {
                            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                            intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            kotlin.system.exitProcess(0)
                        } catch (e: Exception) {
                            android.util.Log.e("ProfileScreen", "Failed to restart app: ${e.message}")
                        }
                    }
                }
            }
            null -> {}
        }
    }

    val scope = rememberCoroutineScope()
    val toasterState = rememberToasterState()

    val bottomSheetState = rememberModalBottomSheetState(
        initialDetent = Hidden, detents = listOf(Hidden, FullyExpanded)
    )

    val changePassSheetState = rememberModalBottomSheetState(
        initialDetent = Hidden, detents = listOf(Hidden, FullyExpanded)
    )

    val logoutDialogState = remember { mutableStateOf(false) }

    val dialogState = rememberDialogState(initiallyVisible = false)

    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        store.sendAction(ProfileAction.GetUser)
    }

    if (state.isLoading || languageState.isLoading) {
        LoadingDialog()
    }

    if (state.error != null) {
        ErrorDialog(throwable = state.error, onDismissRequest = {
            store.sendAction(ProfileAction.DismissError)
        })
    }
    
    if (languageState.error != null) {
        ErrorDialog(throwable = languageState.error, onDismissRequest = {
            languageStore.sendAction(LanguageAction.DismissError)
        })
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Toaster(state = toasterState)
        PullToRefreshBox(modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = false,
            onRefresh = {}) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = "Hồ sơ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                AsyncImage(
                    model = state.user?.avatar,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize(0.35f)
                        .aspectRatio(1.0f)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(
                            Color.White, shape = CircleShape
                        )
                        .align(Alignment.CenterHorizontally),
                    contentDescription = null,
                    error = painterResource(R.drawable.ic_default_avatar)
                )

                Spacer(modifier = Modifier.height(16.dp))

                state.user?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                state.user?.email?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingItem(icon = ImageVector.vectorResource(R.drawable.ic_edit_profile),
                    title = "Chỉnh sửa hồ sơ",
                    onClick = {
                        store.sendAction(ProfileAction.NavigateUpdateProfile)
                    })

                SettingItem(icon = ImageVector.vectorResource(R.drawable.ic_heart),
                    title = "Hồ sơ sức khỏe",
                    onClick = {
                        store.sendAction(ProfileAction.NavigateHealthRecord)
                    })

//                // Add language setting item
//                SettingItem(
//                    icon = ImageVector.vectorResource(R.drawable.ic_language),
//                    title = "Ngôn ngữ",
//                    subtitle = languageState.currentLanguage.nativeName,
//                    onClick = {
//                        showLanguageDialog = true
//                    }
//                )

                SettingItem(icon = ImageVector.vectorResource(R.drawable.ic_setting),
                    title = "Cài đặt",
                    onClick = {
                        scope.launch {
                            bottomSheetState.animateTo(FullyExpanded)
                        }
                    })

                SettingItem(icon = ImageVector.vectorResource(R.drawable.ic_help),
                    title = "Trợ giúp & hỗ trợ",
                    onClick = {
                    })

                SettingItem(icon = ImageVector.vectorResource(R.drawable.ic_term),
                    title = "Điều khoản & Quyền riêng tư",
                    onClick = {
                    })

                SettingItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_logout),
                    title = "Đăng xuất",
                    onClick = {
                        logoutDialogState.value = true
                    },
                    bottomDivider = false
                )
            }

            if (logoutDialogState.value) {
                ConfirmBottomSheet(
                    title = "Đăng xuất",
                    content = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này?",
                    onConfirm = {
                        store.sendAction(ProfileAction.Logout)
                        logoutDialogState.value = false
                    },
                    onDismiss = {
                        logoutDialogState.value = false
                    },
                    confirmButtonText = "Đăng xuất",
                    dismissButtonText = "Hủy",
                )
            }

            SettingBottomSheet(appState = state, languageStore = languageStore, prefsStorage = prefsStorage, state = bottomSheetState, onChangeChatServer = {
                store.sendAction(ProfileAction.ChangeChatServer(it))
            }, onChangeBackendServer = {
                store.sendAction(ProfileAction.ChangeBackendServer(it))
            })
        }
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String? = "",
    subtitle: String? = null,
    onClick: () -> Unit,
    trailingIcon: Boolean = true,
    bottomDivider: Boolean = true,
) {
    Row(modifier = modifier
        .padding(top = 8.dp)
        .clickable {
            onClick()
        }, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                maxLines = 1,
            )
            
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }

        if (trailingIcon) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }

    if (bottomDivider) {
        HorizontalDivider(
            thickness = 1.dp, color = Grey_200
        )
    }
}
