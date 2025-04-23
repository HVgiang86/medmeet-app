package com.huongmt.medmeet.ui.auth

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.huongmt.medmeet.shared.app.AuthStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface AuthDestination {
    object SignUp : Screen, AuthDestination, KoinComponent {
        @Composable
        override fun Content() {
            val store: AuthStore by inject()
            SignUpScreen(store = store)
        }
    }

    object Login : Screen, AuthDestination, KoinComponent {
        @Composable
        override fun Content() {
            val store: AuthStore by inject()
            LoginScreen(store = store)
        }
    }
}

@Composable
fun AuthNavigation(destination: AuthDestination) {
    Navigator(screen = destination as Screen)
}