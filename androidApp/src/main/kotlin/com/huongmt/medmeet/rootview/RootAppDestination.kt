package com.huongmt.medmeet.rootview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.component.DrawerMenu
import com.huongmt.medmeet.shared.app.AuthStore
import com.huongmt.medmeet.shared.app.chat.ChatStore
import com.huongmt.medmeet.ui.auth.AuthScreen
import com.huongmt.medmeet.ui.chat.ChatScreen
import com.huongmt.medmeet.ui.main.MainScreen
import com.huongmt.medmeet.ui.main.nav.MainScreenNavigation
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RootAppDestination {
    object Home : Screen, RootAppDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            MainScreenNavigation(onLogout = {

            })

//            val chatStore: ChatStore by inject()
//
//            val scaffoldState = rememberScaffoldState()
//            val scope = rememberCoroutineScope()
//
//            Scaffold(scaffoldState = scaffoldState, snackbarHost = { hostState ->
//                SnackbarHost(
//                    hostState = hostState,
//                    modifier =
//                    Modifier.padding(
//                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
//                    ),
//                )
//            }) { contentPadding ->
//                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//
//                ModalNavigationDrawer(
//                    drawerContent = {
//                        DrawerMenu(onClose = {
//                            Napier.d { "Close drawer" }
//                        }, store = chatStore)
//                    },
//                    drawerState = drawerState,
//                ) {
//                    Box(
//                        modifier =
//                        Modifier
//                            .padding(contentPadding)
//                            .statusBarsPadding(),
//                    ) {
//                        ChatScreen(
//                            onOpenDrawer = {
//                                scope.launch {
//                                    drawerState.apply {
//                                        if (isClosed) {
//                                            Napier.d { "Open drawer" }
//                                            open()
//                                        } else {
//                                            Napier.d { "Close drawer" }
//                                            close()
//                                        }
//                                    }
//                                }
//                            },
//                            store = chatStore,
//                        )
//
//                    }
//                }
//            }
        }
    }

    object Login : Screen, RootAppDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val authStore: AuthStore by inject()

            AuthScreen(
                store = authStore,
                onNavigateMain = {
                    navigator.replace(Home)
                },
            )
        }
    }

    object OnBoarding : Screen, RootAppDestination {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow

        }
    }
}

@Composable
fun RootAppNavigation(startDestination: RootAppDestination) {
    Navigator(screen = startDestination as Screen)
}
