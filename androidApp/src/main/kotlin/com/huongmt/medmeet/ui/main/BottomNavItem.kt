package com.huongmt.medmeet.ui.main

import androidx.annotation.DrawableRes
import com.huongmt.medmeet.R
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination

enum class BottomNavItem(@DrawableRes val selectedIcon: Int, @DrawableRes val unselectedIcon: Int) {
    HOME(
        selectedIcon = R.drawable.ic_home_enable, unselectedIcon = R.drawable.ic_home_disable
    ),
    NOTIFICATION(
        selectedIcon = R.drawable.ic_notification_enable,
        unselectedIcon = R.drawable.ic_notification_disable
    ),
    CALENDAR(
        selectedIcon = R.drawable.ic_calendar_enable,
        unselectedIcon = R.drawable.ic_calendar_disable
    ),
    PROFILE(
        selectedIcon = R.drawable.ic_profile_enable, unselectedIcon = R.drawable.ic_profile_disable
    ), ;

    fun asTopLevelDestination(): MainScreenDestination {
        return when (this) {
            HOME -> MainScreenDestination.Home
            NOTIFICATION -> MainScreenDestination.Notification
            PROFILE -> MainScreenDestination.Profile()
            CALENDAR -> MainScreenDestination.Schedule
        }
    }
}

fun MainScreenDestination.asBottomNavItem(): BottomNavItem {
    return when (this) {
        MainScreenDestination.Home -> BottomNavItem.HOME
        MainScreenDestination.Notification -> BottomNavItem.NOTIFICATION
        is MainScreenDestination.Profile -> BottomNavItem.PROFILE
        MainScreenDestination.Schedule -> BottomNavItem.CALENDAR
        else -> BottomNavItem.HOME
    }
}

