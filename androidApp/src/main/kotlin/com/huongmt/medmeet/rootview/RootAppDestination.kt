package com.huongmt.medmeet.rootview

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.shared.app.AuthStore
import com.huongmt.medmeet.ui.auth.AuthScreen
import com.huongmt.medmeet.ui.main.nav.MainScreenNavigation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RootAppDestination {
    object Home : Screen, RootAppDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            MainScreenNavigation(onLogout = {
                navigator.replaceAll(Login)
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
//                                scope.runFlow {
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
