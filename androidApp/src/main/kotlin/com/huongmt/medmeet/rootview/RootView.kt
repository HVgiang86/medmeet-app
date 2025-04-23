package com.huongmt.medmeet.rootview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.shared.app.RootAction
import com.huongmt.medmeet.shared.app.RootEffect
import com.huongmt.medmeet.shared.app.RootStore
import com.huongmt.medmeet.theme.AppTheme
import com.huongmt.medmeet.utils.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootView : Screen, KoinComponent {
    @Composable
    override fun Content() {
        val store: RootStore by inject()
        val effect = store.observeSideEffect().collectAsState(initial = null)

        LaunchedEffect(Unit) {
            store.sendAction(RootAction.Init)
        }

        AppTheme {
            Box(
                Modifier.padding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                        .asPaddingValues(),
                ),
            ) {
                when (val effectValue = effect.value) {
                    is RootEffect.ShowError -> {
                        ErrorDialog(
                            throwable = effectValue.error,
                        )
                    }

                    RootEffect.ShowLogin -> {
                        RootAppNavigation(startDestination = RootAppDestination.Login)
                        Logger.d("ShowLogin")
                        store.sendAction(RootAction.SetLoggedIn(true))
                    }

                    RootEffect.ShowMain -> {
                        RootAppNavigation(startDestination = RootAppDestination.Home)
                    }

                    RootEffect.ShowOnBoarding -> {
                        RootAppNavigation(startDestination = RootAppDestination.OnBoarding)
                        Logger.d("ShowOnBoarding")
                        store.sendAction(RootAction.SetFirstRun(false))
                    }

                    null -> {

                    }
                }
            }
        }
    }
}
