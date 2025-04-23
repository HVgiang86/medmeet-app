package com.huongmt.medmeet.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.FailDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.AuthEffect
import com.huongmt.medmeet.shared.app.AuthStore

@Composable
fun AuthScreen(store: AuthStore, onNavigateMain: () -> Unit) {
    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(initial = null)

    LaunchedEffect(Unit) {
//        viewModel.sendEvent(AuthEvent.Init)
    }

    when (val sideEffect = effect) {
        is AuthEffect.NavigateMain -> {
            onNavigateMain()
        }

        null -> {}
        AuthEffect.NavigateLogin -> {
            AuthNavigation(AuthDestination.Login)
        }
        AuthEffect.NavigateSignUp -> {
            AuthNavigation(AuthDestination.SignUp)
        }
        is AuthEffect.ShowError -> {
            ErrorDialog(throwable = sideEffect.error)
        }
        is AuthEffect.ShowValidateError -> {

        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    if (state.validateError != null) {
        FailDialog(title = "Error", content = state.validateError?.throwable?.message ?: "Please check your input", cancelable = true, onCanceled = {
            viewModel.sendEvent(AuthEvent.DismissValidateError)
        }, onBtnClick = {
            viewModel.sendEvent(AuthEvent.DismissValidateError)
        })
    }
}
