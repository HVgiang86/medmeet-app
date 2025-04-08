package com.gianghv.kmachat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.gianghv.kmachat.component.DrawerMenu
import com.gianghv.kmachat.ui.chat.ChatScreen
import com.gianghv.kmachat.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    var isDrawerOpen by remember { mutableStateOf(false) }
    
    ModalNavigationDrawer(
        drawerContent = {
            DrawerMenu(
                onClose = { isDrawerOpen = false }
            )
        },
        drawerState = rememberDrawerState(
            initialValue = if (isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
        )
    ) {
        ChatScreen(
            onOpenDrawer = { isDrawerOpen = true }
        )
    }
} 