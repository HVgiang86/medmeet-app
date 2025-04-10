package com.gianghv.kmachat

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.gianghv.kmachat.component.DrawerMenu
import com.gianghv.kmachat.ui.chat.ChatScreen
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent

class HomeScreen : Screen, KoinComponent {
    @Composable
    override fun Content() {
        Napier.d { "HomeScreen Content" }
        val scaffoldState = rememberScaffoldState()
        Scaffold(scaffoldState = scaffoldState, snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState, modifier = Modifier.padding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues()
                )
            )
        }) { contentPadding ->
            var isDrawerOpen by remember { mutableStateOf(false) }

            ModalNavigationDrawer(
                drawerContent = {
                    DrawerMenu(onClose = { isDrawerOpen = false })
                }, drawerState = rememberDrawerState(
                    initialValue = if (isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
                )
            ) {
                Napier.d { "HomeScreen Content1" }
                Navigator(ChatScreen(onOpenDrawer = { isDrawerOpen = true }))
            }

        }

    }
}
