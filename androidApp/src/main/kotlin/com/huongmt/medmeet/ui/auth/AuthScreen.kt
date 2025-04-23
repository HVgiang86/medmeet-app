package com.huongmt.medmeet.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.FailDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.AuthAction
import com.huongmt.medmeet.shared.app.AuthEffect
import com.huongmt.medmeet.shared.app.AuthScreens
import com.huongmt.medmeet.shared.app.AuthStore
import io.github.aakira.napier.Napier

@Composable
fun AuthScreen(store: AuthStore, onNavigateMain: () -> Unit) {
    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        store.sendAction(AuthAction.Init)
    }

    LaunchedEffect(effect) {
        when (effect) {
            is AuthEffect.NavigateMain -> {
                onNavigateMain()
            }

            null -> {}
        }
    }

    if (state.error != null) {
        Napier.d("Error: ${state.error}")
        ErrorDialog(throwable = state.error!!, onDismissRequest = {
            store.sendAction(AuthAction.DismissError)
        })
    }

    if (state.validateError != null) {
        FailDialog(title = "Error",
            content = state.validateError?.message ?: "Please check your input",
            cancelable = true,
            onCanceled = {
                store.sendAction(AuthAction.DismissError)
            },
            onBtnClick = {
                store.sendAction(AuthAction.DismissError)
            })
    }

    when (state.currentScreen) {
        AuthScreens.Login -> LoginScreen(store = store)
        AuthScreens.SignUp -> SignUpScreen(store = store)
    }

    if (state.isLoading) {
        LoadingDialog()
    }
}
