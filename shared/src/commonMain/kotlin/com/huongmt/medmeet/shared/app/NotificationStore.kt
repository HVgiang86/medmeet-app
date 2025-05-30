package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.AppNotification
import com.huongmt.medmeet.shared.core.entity.NotificationAction
import com.huongmt.medmeet.shared.core.entity.NotificationDetailRoute
import com.huongmt.medmeet.shared.core.entity.NotificationType
import com.huongmt.medmeet.shared.core.entity.getNotificationDetailRoute
import com.huongmt.medmeet.shared.core.repository.BookingRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import com.huongmt.medmeet.shared.utils.ext.toHM
import io.github.aakira.napier.Napier

// State
data class NotificationState(
    val isLoading: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val error: Throwable? = null
) : Store.State(isLoading)

// Action
sealed interface NotificationUiAction : Store.Action {
    data object LoadNotifications : NotificationUiAction
    data class LoadNotificationsSuccess(val notifications: List<AppNotification>) : NotificationUiAction
    data class ShowError(val error: Throwable) : NotificationUiAction
    data object DismissError : NotificationUiAction
    data class ClickNotification(val notification: AppNotification) : NotificationUiAction
    data class GetNotificationContentSuccess(val notificationId: String, val content: String) : NotificationUiAction
}

// Effect
sealed interface NotificationEffect : Store.Effect {
    data class NavigateToProfile(val userId: String?) : NotificationEffect
    data class NavigateToHealthRecord(val entityId: String?) : NotificationEffect
    data class NavigateToBookingDetail(val entityId: String?) : NotificationEffect
}

class NotificationStore(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository
) : Store<NotificationState, NotificationUiAction, NotificationEffect>(
    initialState = NotificationState()
) {
    override val onException: (Throwable) -> Unit = {
        sendAction(NotificationUiAction.ShowError(it))
    }

    override fun dispatch(oldState: NotificationState, action: NotificationUiAction) {
        when (action) {
            NotificationUiAction.LoadNotifications -> {
                setState(oldState.copy(isLoading = true, error = null))
                loadNotifications()
            }
            is NotificationUiAction.LoadNotificationsSuccess -> {
                setState(oldState.copy(isLoading = false, notifications = action.notifications, error = null))
                Napier.d { "Load notifications\n${action.notifications}" }
            }
            is NotificationUiAction.ShowError -> {
                setState(oldState.copy(isLoading = false, error = action.error))
            }
            NotificationUiAction.DismissError -> {
                setState(oldState.copy(error = null))
            }
            is NotificationUiAction.ClickNotification -> {
                val notification = action.notification
                val route = getNotificationDetailRoute(notification.type, notification.action)
                when (route) {
                    NotificationDetailRoute.GoToUserProfile -> setEffect(NotificationEffect.NavigateToProfile(notification.updatedByUserId))
                    NotificationDetailRoute.GoToHealthRecord -> setEffect(NotificationEffect.NavigateToHealthRecord(notification.entityId))
                    NotificationDetailRoute.GoToBookingDetail -> setEffect(NotificationEffect.NavigateToBookingDetail(notification.entityId))
                    NotificationDetailRoute.NoAction -> {}
                }
            }

            is NotificationUiAction.GetNotificationContentSuccess -> {
                val notificationId = action.notificationId
                val content = action.content
                val notifications = oldState.notifications.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(content = content)
                    } else {
                        notification
                    }
                }
                setState(oldState.copy(notifications = notifications))
            }
        }
    }

    private fun loadNotifications() {
        runFlow(exception = coroutineExceptionHandler {}) {
            userRepository.getLocalUserId().collect { userId ->
                userRepository.getAppNotification(userId).collect { notifications ->
                    sendAction(NotificationUiAction.LoadNotificationsSuccess(notifications))
                    getNotificationContent(notifications)
                }
            }
        }
    }

    private fun getNotificationContent(notifications: List<AppNotification>) {
        runFlow(
            exception = coroutineExceptionHandler {
            }
        ) {
            notifications.forEach { notification ->
                val content = notification.content
                if (content == null) {
                    when (notification.type) {
                        NotificationType.MEDICAL_CONSULTATION_HISTORY -> {
                            if (notification.entityId != null) {
                                bookingRepository.getBookingDetail(notification.entityId).collect { booking ->
                                    when (notification.action) {
                                        NotificationAction.CREATE -> {
                                            val contentStr = "Booking ${booking.medicalServiceName} at ${booking.clinicSchedule.startTime.toHM()}"
                                            sendAction(NotificationUiAction.GetNotificationContentSuccess(notification.id, contentStr))
                                        }
                                        NotificationAction.UPDATE -> {
                                            val contentStr = "Schedule ${booking.code}: ${notification.details}"
                                            sendAction(NotificationUiAction.GetNotificationContentSuccess(notification.id, contentStr))
                                        }
                                        NotificationAction.DELETE -> {
                                            val contentStr = "Schedule ${booking.code}: ${notification.details}"
                                            sendAction(NotificationUiAction.GetNotificationContentSuccess(notification.id, contentStr))
                                        }
                                        null -> {
                                            /* no-op */
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            /* no-op */
                        }
                    }
                }
            }
        }
    }
} 
