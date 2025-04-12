package com.gianghv.kmachat.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.fillMaxWidth(0.8f),
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            "KMA Chat",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Divider()
        
        NavigationDrawerItem(
            label = { Text("New Chat") },
            selected = false,
            onClick = {
                // TODO: Implement new chat functionality
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = {
                // TODO: Implement settings navigation
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = {
                // TODO: Implement about navigation
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
} 