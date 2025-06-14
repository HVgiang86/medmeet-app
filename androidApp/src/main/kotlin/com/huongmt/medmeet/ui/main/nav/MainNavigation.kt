package com.huongmt.medmeet.ui.main.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.component.FailDialog
import com.huongmt.medmeet.shared.app.ChatStore
import com.huongmt.medmeet.shared.app.ClinicDetailStore
import com.huongmt.medmeet.shared.app.HomeStore
import com.huongmt.medmeet.shared.app.ProfileStore
import com.huongmt.medmeet.shared.app.ScheduleStore
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.app.BookingDetailStore
import com.huongmt.medmeet.shared.app.HealthRecordStore
import com.huongmt.medmeet.shared.app.LanguageStore
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.ui.booking.BookingScreen
import com.huongmt.medmeet.ui.bookingdetail.BookingDetailScreen
import com.huongmt.medmeet.ui.chat.ChatScreenContent
import com.huongmt.medmeet.ui.clinicdetail.ClinicDetailScreen
import com.huongmt.medmeet.ui.home.HomeScreen
import com.huongmt.medmeet.ui.main.MainScreen
import com.huongmt.medmeet.ui.notification.NotificationScreen
import com.huongmt.medmeet.ui.profile.ProfileScreen
import com.huongmt.medmeet.ui.schedule.ScheduleScreen
import com.huongmt.medmeet.ui.healthrecord.HealthRecordScreen
import com.huongmt.medmeet.ui.profiledetail.ProfileDetailScreen
//import com.huongmt.medmeet.ui.profiledetail.ProfileDetailScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


interface TopLevelScreenDestination : MainScreenDestination {
    companion object {
        fun getStartScreen(): TopLevelScreenDestination = MainScreenDestination.Home
    }
}

abstract class LogoutFromDestination(open var onLogout: (() -> Unit) = {}) : MainScreenDestination

interface MainScreenDestination {
    object Home : Screen, TopLevelScreenDestination, KoinComponent {
        @Composable
        override fun Content() {
            val store: HomeStore by inject()
            val navigator = LocalNavigator.currentOrThrow
            HomeScreen(store = store, navigateTo = { destination ->
                navigator.navigate(destination)
            })
        }
    }

    object Notification : Screen, TopLevelScreenDestination, KoinComponent {
        @Composable

        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            NotificationScreen(navigateBack = {
                navigator.pop()
            }, navigateTo = {
                navigator.navigate(it)
            }).Content()
        }
    }


    object ChatScreen : Screen, KoinComponent, MainScreenDestination {
        @Composable
        override fun Content() {
            val chatStore: ChatStore by inject()
            val navigator = LocalNavigator.currentOrThrow
            ChatScreenContent(chatStore, onBack = {
                navigator.pop()
            })
        }
    }

    object Schedule : Screen, TopLevelScreenDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val store: ScheduleStore by inject()
            ScheduleScreen(store = store, navigateBack = {
                navigator.pop()
            }, navigateTo = { destination ->
                navigator.navigate(destination)
            })
        }
    }

    object HealthRecord : Screen, MainScreenDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val store: HealthRecordStore by inject()

            HealthRecordScreen(store = store, navigateBack = {
                navigator.pop()
            })
        }
    }

    class Profile : Screen, TopLevelScreenDestination, KoinComponent, LogoutFromDestination() {
        @Composable
        override fun Content() {
            val store: ProfileStore by inject()
            val languageStore: LanguageStore by inject()
            val prefsStorage: PrefsStorage by inject()
            val navigator = LocalNavigator.currentOrThrow
            ProfileScreen(
                store = store,
                languageStore = languageStore,
                prefsStorage = prefsStorage,
                navigateTo = {
                    navigator.navigate(it)
                },
                onLogout = {
                    onLogout()
                })
        }
    }

    class ProfileDetail : Screen, MainScreenDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ProfileDetailScreen().Content()
//            FailDialog(
//                title = "Error",
//                content = "Trang này đang tạm khoá!",
//                cancelable = true,
//                onCanceled = {
//                    navigator.pop()
//                },
//                onBtnClick = {
//                    navigator.pop()
//                },
//                btnText = "Đồng ý"
//            )
        }
    }

    class ClinicDetail(private val clinic: Clinic) : Screen, MainScreenDestination, KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val store: ClinicDetailStore by inject()
            ClinicDetailScreen(store = store, clinicId = clinic.id, navigateBack = {
                navigator.pop()
            }, navigateTo = {
                navigator.navigate(it)
            })
        }
    }

    class BookingAppointment(private val clinic: Clinic) : Screen, MainScreenDestination,
        KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val store: BookingStore by inject()
            BookingScreen(store = store, clinic = clinic, onBack = {
                navigator.pop()
            }, goHome = {
                navigator.replaceAll(Home)
            }, goToDetail = { bookingId ->
                navigator.replace(BookingDetail(bookingId))
            })
        }
    }

    class BookingDetail(private val bookingId: String) : Screen, MainScreenDestination,
        KoinComponent {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            BookingDetailScreen(bookingId = bookingId).Content()
        }
    }
}

fun MainScreenDestination.isTopLevelScreen() = this is TopLevelScreenDestination

fun MainScreenDestination.isLogoutFromScreen() = this is LogoutFromDestination

fun Navigator.navigate(destination: MainScreenDestination) {
    val destinationScreen = destination as Screen
    when {
        destination.isTopLevelScreen() && destination == TopLevelScreenDestination.getStartScreen() -> this.replaceAll(
            destinationScreen
        )

        destination.isTopLevelScreen() -> with(this) {
            popUntilRoot()
            push(destinationScreen)
        }

        else -> this.push(destinationScreen)
    }
}

@Composable
fun MainScreenNavigation(onLogout: () -> Unit) {
    val startScreen = TopLevelScreenDestination.getStartScreen() as Screen
    Navigator(screen = startScreen) { navigator ->
        val currentScreen = navigator.lastItem
        val currentDestination = currentScreen as MainScreenDestination
        MainScreen(currentDestination = currentDestination, onDestinationChanged = {
            navigator.navigate(it)
        }, onNavigateBack = {
            navigator.pop()
        }, onNavigateTo = {
            navigator.navigate(it as MainScreenDestination)
        }, onLogout = {
            onLogout()
        }) {
            AnimatedTransition(navigator)
        }
    }
}

@Composable
private fun AnimatedTransition(navigator: Navigator) {
    AnimatedContent(targetState = navigator.lastItem, transitionSpec = {

        val (initialScale, targetScale) = when (navigator.lastEvent) {
            StackEvent.Pop -> 1f to 0.85f
            else -> 0.85f to 1f
        }

        val stiffness = Spring.StiffnessMediumLow
        val enterTransition = fadeIn(tween(easing = EaseIn)) + scaleIn(
            spring(stiffness = stiffness), initialScale = initialScale
        )

        val exitTransition = fadeOut(spring(stiffness = stiffness)) + scaleOut(
            tween(easing = EaseOut), targetScale = targetScale
        )

        enterTransition togetherWith exitTransition
    }, label = "") { currentScreen ->
        navigator.saveableState("transition", currentScreen) {
            currentScreen.Content()
        }
    }
}
