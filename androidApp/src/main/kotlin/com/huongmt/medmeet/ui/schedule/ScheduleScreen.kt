package com.huongmt.medmeet.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.shared.app.ScheduleAction
import com.huongmt.medmeet.shared.app.ScheduleEffect
import com.huongmt.medmeet.shared.app.ScheduleState
import com.huongmt.medmeet.shared.app.ScheduleStore
import com.huongmt.medmeet.shared.app.ScheduleTab
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.theme.CardShapeDefault
import com.huongmt.medmeet.theme.Grey_200
import com.huongmt.medmeet.theme.Grey_600
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toHMS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    store: ScheduleStore,
    navigateBack: () -> Unit,
    navigateTo: ((MainScreenDestination) -> Unit)? = null
) {
    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        store.sendAction(ScheduleAction.LoadAppointments(showTab = ScheduleTab.UPCOMING))
    }

    LaunchedEffect(effect) {
        when (effect) {
            ScheduleEffect.NavigateBack -> navigateBack()
            is ScheduleEffect.ShowCancelSuccess -> {
                // You can show a toast or snackbar here
                // For now, just dismiss the effect
            }
            null -> {}
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    if (state.error != null) {
        ErrorDialog(throwable = state.error) {
            store.sendAction(ScheduleAction.DismissError)
        }
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
                text = "Lịch khám của tôi",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScheduleTabs(state = state,
                selectedTab = state.selectedTab,
                onTabSelected = { tab -> store.sendAction(ScheduleAction.SelectTab(tab)) })

            AppointmentsList(
                appointments = state.displayAppointments,
                onDownloadClick = { appointmentId ->

                },
                onAppointmentClick = { appointmentId ->
                    navigateTo?.invoke(MainScreenDestination.BookingDetail(appointmentId))
                },
                onCancelClick = { appointmentId ->
                    store.sendAction(ScheduleAction.CancelAppointment(appointmentId))
                },
                isCanceling = state.isCanceling
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTabs(
    state: ScheduleState,
    selectedTab: ScheduleTab,
    onTabSelected: (ScheduleTab) -> Unit,
) {

    val tabs = listOf(ScheduleTab.UPCOMING, ScheduleTab.COMPLETED, ScheduleTab.CANCELED)
    PrimaryTabRow(selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        divider = {
            HorizontalDivider(thickness = 1.dp, color = Grey_200)
        }) {
        tabs.forEach { tab ->
            Tab(selected = selectedTab == tab, onClick = { onTabSelected(tab) }, text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val count = when (tab) {
                        ScheduleTab.UPCOMING -> state.numberOfPending
                        ScheduleTab.COMPLETED -> state.numberOfCompleted
                        ScheduleTab.CANCELED -> state.numberOfCanceled
                    }

                    val nameDisplay = when (tab) {
                        ScheduleTab.UPCOMING -> "Đang chờ"
                        ScheduleTab.COMPLETED -> "Đã khám"
                        ScheduleTab.CANCELED -> "Đã hủy"
                    }

                    Text(
                        text = nameDisplay,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )

                    if (count > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary, shape = CircleShape
                                )
                        ) {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            })
        }
    }
}

@Composable
private fun AppointmentsList(
    appointments: List<MedicalConsultationHistory>,
    onDownloadClick: (String) -> Unit,
    onAppointmentClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    isCanceling: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(appointments.size + 1) { index ->
            val appointment = if (index < appointments.size) {
                appointments[index]
            } else {
                null
            }
            if (appointment == null) {
                Spacer(modifier = Modifier.height(60.dp))
                return@items
            }
            AppointmentCard(
                appointment = appointment,
                onDownloadClick = { onDownloadClick(appointment.id) },
                onClick = { onAppointmentClick(appointment.id) },
                onCancelClick = { onCancelClick(appointment.id) },
                isCanceling = isCanceling
            )
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: MedicalConsultationHistory,
    onDownloadClick: () -> Unit,
    onClick: () -> Unit,
    onCancelClick: () -> Unit,
    isCanceling: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clickable(onClick = onClick),
        shape = CardShapeDefault,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Booking ID
            Text(
                text = "Mã phiếu khám: #${appointment.code}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Grey_200)

            // Clinic name
            appointment.clinic.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    minLines = 2,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Service
            appointment.medicalServiceName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Clinic location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .width(16.dp)
                        .align(Alignment.Top)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${appointment.clinic.address}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and time
            Text(
                text = "Ngày khám: ${appointment.examinationDate?.toDMY()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey_600
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Giờ khám: ${appointment.clinicSchedule.startTime.toHMS()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey_600
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    text = "Trạng thái:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (appointment.status) {
                        MedicalRecordStatus.PENDING -> "Đặt khám thành công"
                        MedicalRecordStatus.COMPLETED -> "Đã khám"
                        MedicalRecordStatus.CANCELED -> "Đã hủy"
                        null -> "Unknown"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (appointment.status) {
                        MedicalRecordStatus.PENDING -> Color(0xFF4CAF50)
                        MedicalRecordStatus.COMPLETED -> Color(0xFF2196F3)
                        MedicalRecordStatus.CANCELED -> Color(0xFFF44336)
                        null -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Grey_200)

            // Status
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Cancel button for pending appointments
                if (appointment.status == MedicalRecordStatus.PENDING) {
                    PrimaryButton(
                        onClick = onCancelClick,
                        modifier = Modifier.wrapContentSize(),
                        text = {
                                Text(
                                    text = "Hủy lịch",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                        }
                    )
                }

//                // Download button only for completed appointments
//                if (appointment.status == MedicalRecordStatus.COMPLETED) {
//                    PrimaryButton(
//                        onClick = onDownloadClick,
//                        modifier = Modifier.wrapContentSize(),
//                        text = {
//                            Text(
//                                text = "Tái khám", style = MaterialTheme.typography.bodyMedium
//                            )
//                        })
//                }
            }
        }
    }
}

// Extension function to position the tab indicator
fun Modifier.itabIndicatorOffset(
    currentTabPosition: LayoutCoordinates,
): Modifier = this.then(
    Modifier.padding(
        start = currentTabPosition.positionInParent().x.dp, end = 0.dp
    )
) 