package com.gianghv.kmachat.composeui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gianghv.kmachat.component.DrawerMenu
import com.gianghv.kmachat.shared.app.FeedAction
import com.gianghv.kmachat.shared.app.FeedStore
import com.gianghv.kmachat.shared.app.chat.ChatStore
import com.gianghv.kmachat.ui.chat.ChatScreen
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeScreen :
    Screen,
    KoinComponent {
    @Composable
    override fun Content() {
        val store: ChatStore by inject()

        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        Scaffold(scaffoldState = scaffoldState, snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState,
                modifier =
                    Modifier.padding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
                    ),
            )
        }) { contentPadding ->
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            ModalNavigationDrawer(
                drawerContent = {
                    DrawerMenu(onClose = {
                        Napier.d { "Close drawer" }
                    }, store = store)
                },
                drawerState = drawerState,
            ) {
                Box(
                    modifier =
                        Modifier
                            .padding(contentPadding)
                            .statusBarsPadding(),
                ) {
                    Navigator(
                        ChatScreen(store = store, onOpenDrawer = {
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
                        }),
                    )
                }
            }
        }
    }
}

class MainScreen :
    Screen,
    KoinComponent {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val store: FeedStore by inject()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val state by store.observeState().collectAsState()
        val refreshState =
            rememberPullRefreshState(refreshing = state.progress, onRefresh = {
                store.sendAction(FeedAction.Refresh(false))
            })

        LaunchedEffect(Unit) {
            store.sendAction(FeedAction.Refresh(false))
        }
        Box(modifier = Modifier.pullRefresh(refreshState)) {
            MainFeed(store = store, onPostClick = { post ->
                post.link?.let { url ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }, onEditClick = {
                navigator.push(FeedListScreen())
            })
            PullRefreshIndicator(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                refreshing = state.progress,
                state = refreshState,
                scale = true, // https://github.com/google/accompanist/issues/572
            )
        }
    }
}

class FeedListScreen :
    Screen,
    KoinComponent {
    @Composable
    override fun Content() {
        val store: FeedStore by inject()
        FeedList(store = store)
    }
}
