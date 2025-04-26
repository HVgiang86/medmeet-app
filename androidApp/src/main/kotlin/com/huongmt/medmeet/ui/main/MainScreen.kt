package com.huongmt.medmeet.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.ConfirmChatBotSheet
import com.huongmt.medmeet.data.WholeApp
import com.huongmt.medmeet.theme.Divider_color
import com.huongmt.medmeet.theme.Grey_500
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination
import com.huongmt.medmeet.ui.main.nav.isLogoutFromScreen
import com.huongmt.medmeet.ui.main.nav.isTopLevelScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    currentDestination: MainScreenDestination,
    onDestinationChanged: (MainScreenDestination) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit,
) {
    val openConfirmChatBotSheet = remember { mutableStateOf(false) }
    Scaffold(content = { padding ->
        content()

        if (openConfirmChatBotSheet.value) {
            if (!WholeApp.confirmChatBot) {
                ConfirmChatBotSheet(onConfirm = {
                    onNavigateTo(MainScreenDestination.ChatScreen)
                    openConfirmChatBotSheet.value = false
                    WholeApp.confirmChatBot = true
                }, onDismiss = {
                    openConfirmChatBotSheet.value = false
                })
            } else {
                onNavigateTo(MainScreenDestination.ChatScreen)
                openConfirmChatBotSheet.value = false
            }
        }
    }, floatingActionButton = {
        // Floating Action Button centered on the navigation bar
        Box(
            modifier = Modifier
                .offset(y = (80).dp)
                .background(
                    color = Color.Transparent, shape = CircleShape
                )
                .border(
                    width = 2.dp, color = Color.Transparent, shape = CircleShape
                )
        ) {
            FloatingActionButton(
                onClick = {
                    openConfirmChatBotSheet.value = true
                },
                modifier = Modifier.align(Alignment.Center),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        R.drawable.ic_ai_enable
                    ), contentDescription = "AI Chat", modifier = Modifier.size(24.dp)
                )
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center, bottomBar = {
        if (currentDestination.isTopLevelScreen()) {
            BottomNavigation(selectedNavItem = currentDestination.asBottomNavItem(),
                onNavigationItemSelected = {
                    onDestinationChanged(it.asTopLevelDestination())
                })
        }

        if (currentDestination.isLogoutFromScreen()) {
            (currentDestination as LogoutFromDestination).onLogout = onLogout
        }
    })
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier,
    selectedNavItem: BottomNavItem,
    onNavigationItemSelected: (BottomNavItem) -> Unit,
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        HorizontalDivider(thickness = 1.dp, color = Divider_color)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background, modifier = modifier.height(80.dp)
        ) {
            val navItems = BottomNavItem.entries.toTypedArray()

            for (i in 0..navItems.size) {
                if (i == 2) {
                    NavigationBarItem(
                        modifier = Modifier.size(48.dp),
                        icon = { Box(modifier = Modifier.size(40.dp)) {} },
                        selected = false,
                        onClick = {},
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                        )
                    )
                } else if (i in 0..1) {
                    val item = navItems[i]
                    val isSelected = item == selectedNavItem
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    NavigationBarItem(modifier = Modifier
                        .size(48.dp)
                        .align(alignment = Alignment.CenterVertically),
                        icon = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = CircleShape
                                            )
                                        } else Modifier
                                    )
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        selected = isSelected,
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Grey_500,
                        ),
                        onClick = { onNavigationItemSelected(item) },
                        interactionSource = remember {
                            MutableInteractionSource()
                        })
                } else {
                    val item = navItems[i - 1]
                    val isSelected = item == selectedNavItem
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    NavigationBarItem(modifier = Modifier
                        .size(48.dp)
                        .align(alignment = Alignment.CenterVertically),
                        icon = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = CircleShape
                                            )
                                        } else Modifier
                                    )
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        selected = isSelected,
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Grey_500,
                        ),
                        onClick = { onNavigationItemSelected(item) },
                        interactionSource = remember {
                            MutableInteractionSource()
                        })
                }
            }
        }
    }
}
