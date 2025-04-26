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
