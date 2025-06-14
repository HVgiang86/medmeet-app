package com.huongmt.medmeet.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.component.NotFoundCard
import com.huongmt.medmeet.shared.app.NotificationUiAction
import com.huongmt.medmeet.shared.app.NotificationEffect
import com.huongmt.medmeet.shared.app.NotificationStore
import com.huongmt.medmeet.shared.core.entity.AppNotification
import com.huongmt.medmeet.shared.core.entity.NotificationType
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination
import com.huongmt.medmeet.R
import com.huongmt.medmeet.shared.core.entity.NotificationAction
import com.huongmt.medmeet.theme.Grey_500
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toHMSDMY
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationScreen(
    private val navigateBack: () -> Unit,
    private val navigateTo: (MainScreenDestination) -> Unit
) : KoinComponent {
    private val store: NotificationStore by inject()

    @Composable
    fun Content() {
        val state by store.observeState().collectAsState()
        val effect by store.observeSideEffect().collectAsState(initial = null)

        LaunchedEffect(Unit) {
            store.sendAction(NotificationUiAction.LoadNotifications)
        }

        LaunchedEffect(effect) {
            when (effect) {
                is NotificationEffect.NavigateToProfile -> {
                    navigateTo(MainScreenDestination.Profile())
                }
                is NotificationEffect.NavigateToHealthRecord -> {
                    navigateTo(MainScreenDestination.HealthRecord)
                }
                is NotificationEffect.NavigateToBookingDetail -> {
                    val bookingId = (effect as NotificationEffect.NavigateToBookingDetail).entityId ?: ""
                    navigateTo.invoke(MainScreenDestination.BookingDetail(bookingId))
                }
                null -> {}
            }
        }

        if (state.isLoading) {
            LoadingDialog()
        }

        if (state.error != null) {
            ErrorDialog(throwable = state.error, onDismissRequest = {
                store.sendAction(NotificationUiAction.DismissError)
            })
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = "Thông báo",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (state.notifications.isEmpty() && !state.isLoading) {
                    NotFoundCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        text = "Không tìm thấy thông báo nào"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(state.notifications) { notification ->
                            NotificationItem(notification = notification, onClick = {
                                store.sendAction(NotificationUiAction.ClickNotification(notification))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: AppNotification, onClick: () -> Unit) {
    val userId = WholeApp.USER_ID
    val isMe = notification.updatedByUserId == userId
    val updatedByName = notification.updatedByUser?.name ?: "Unknown"
    val icon = getNotificationIcon(notification.type)
    val title = getNotificationTitle(notification)
    val timestamp = if (notification.createdAt != null) {
        notification.createdAt!!.toHMSDMY()
    } else {
        "Just now"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey_500
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                val notificationContent:String = when {
                    notification.content != null && notification.content!!.isNotEmpty() -> {
                        notification.content ?: ""
                    }

                    notification.content == null && notification.details != null -> notification.details ?: ""

                    else -> ""
                }


                Text(
                    text = notificationContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
                if (!isMe && notification.updatedByUser != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "bởi $updatedByName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun getNotificationIcon(type: NotificationType?): ImageVector {
    return when (type) {
        NotificationType.USER -> ImageVector.vectorResource(R.drawable.ic_profile_enable)
        NotificationType.HEALTH_RECORD -> ImageVector.vectorResource(R.drawable.ic_heart)
        NotificationType.MEDICAL_CONSULTATION_HISTORY -> ImageVector.vectorResource(R.drawable.ic_calendar)
        NotificationType.OTHER, null -> ImageVector.vectorResource(R.drawable.ic_notification_disable)
    }
}

fun getNotificationTitle(notification: AppNotification): String {
    return when (notification.type) {
        NotificationType.USER -> when (notification.action) {
            null -> "User Notification"
            else -> "Cập nhật hồ sơ"
        }
        NotificationType.HEALTH_RECORD -> "Health Record Update"
        NotificationType.MEDICAL_CONSULTATION_HISTORY -> {
            when (notification.action) {
                NotificationAction.CREATE -> {
                    "Đặt khám thành công"
                }
                NotificationAction.UPDATE -> {
                    "Đã cập nhật lịch khám"
                }
                NotificationAction.DELETE -> {
                    "Đã hủy lịch khám"
                }
                null -> {
                    "Thông báo"
                }
            }
        }
        NotificationType.OTHER, null -> "Thông báo"
    }
}