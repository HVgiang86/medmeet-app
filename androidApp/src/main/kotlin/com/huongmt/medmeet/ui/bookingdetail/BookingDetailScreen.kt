package com.huongmt.medmeet.ui.bookingdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.BookingDetailAction
import com.huongmt.medmeet.shared.app.BookingDetailEffect
import com.huongmt.medmeet.shared.app.BookingDetailState
import com.huongmt.medmeet.shared.app.BookingDetailStore
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.theme.Grey_200
import com.huongmt.medmeet.theme.Grey_400
import com.huongmt.medmeet.theme.Grey_500
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toHM
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookingDetailScreen(
    private val bookingId: String
) : Screen, KoinComponent {
    @Composable
    override fun Content() {
        val store: BookingDetailStore by inject()
        val effect = store.observeSideEffect().collectAsState(initial = null)
        val state = store.observeState().collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            store.sendAction(BookingDetailAction.LoadBookingDetail(bookingId))
        }

        LaunchedEffect(effect.value) {
            when (effect.value) {
                BookingDetailEffect.NavigateBack -> {
                    navigator.pop()
                }

                null -> {}
            }
        }

        BookingDetailContent(store = store, state = state.value, onBack = {
            store.sendAction(BookingDetailAction.GoBack)
        })
    }
}

@Composable
fun BookingDetailContent(
    store: BookingDetailStore, state: BookingDetailState, onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        if (state.isLoading) {
            LoadingDialog()
        }

        if (state.error != null) {
            ErrorDialog(throwable = state.error, onDismissRequest = {
                store.sendAction(BookingDetailAction.ClearError)
            })
        }

        Column {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                IconButton(
                    onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Appointment Detail",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Main content
                if (state.detail != null) {
                    AppointmentDetailCard(
                        booking = state.detail!!,
                        doctor = state.doctor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentDetailCard(
    booking: MedicalConsultationHistory, doctor: User?, modifier: Modifier = Modifier
) {
    // Ticket style card
    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Clinic and Service info
            ClinicInfoSection(booking)

            Spacer(modifier = Modifier.height(12.dp))

            // Status
            AppointmentStatusSection(booking.status ?: MedicalRecordStatus.PENDING)

            Spacer(modifier = Modifier.height(16.dp))

            // Barcode and ID
            BarcodeSection(booking.code)

            Spacer(modifier = Modifier.height(16.dp))

            // Divider with dash line pattern
            DashedDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // Doctor info
            DoctorInfoSection(doctor)

            Spacer(modifier = Modifier.height(16.dp))

            // Appointment details
            AppointmentDetailsSection(booking)

            Spacer(modifier = Modifier.height(16.dp))

            // Examination Results
            if (!(booking.diagnosis.isNullOrEmpty() && booking.noteFromDoctor.isNullOrEmpty() && booking.patientStatus.isNullOrEmpty())) {
                ExaminationResultsSection(booking)
            }

//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Re-examination Calendar
//            ReExaminationCalendarSection(booking)
        }
    }

}

@Composable
fun ClinicInfoSection(booking: MedicalConsultationHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
    ) {
        // Clinic logo/avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Grey_200)
        ) {
            AsyncImage(
                model = booking.clinic.logo,
                contentDescription = "Clinic logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_clinic_default),
                placeholder = painterResource(id = R.drawable.ic_clinic_default)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = booking.clinic.name ?: "Clinic name",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = booking.medicalServiceName ?: "Service name",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey_500
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(16.dp),
                    tint = Grey_500
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = booking.clinic.address ?: "Address",
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey_500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AppointmentStatusSection(status: MedicalRecordStatus) {
    val (backgroundColor, textColor) = when (status) {
        MedicalRecordStatus.COMPLETED -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        MedicalRecordStatus.PENDING -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        MedicalRecordStatus.CANCELED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = textColor
        )
    }
}

@Composable
fun BarcodeSection(code: String) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barcode
        Barcode(
            value = code,
            type = BarcodeType.CODE_128,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            height = 80.dp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ID Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mã phiếu", style = MaterialTheme.typography.bodyMedium, color = Grey_500
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "#$code",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DoctorInfoSection(doctor: User?) {
    if (doctor == null) return

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Appointment details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
        ) {
            // Doctor avatar
            AsyncImage(
                model = doctor.avatar,
                contentDescription = "Doctor avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_default_avatar),
                placeholder = painterResource(id = R.drawable.ic_default_avatar)
            )

            Spacer(modifier = Modifier.width(12.dp))


            Column {
                val titleName = StringBuilder()
                if (doctor.qualification != null) {
                    titleName.append(doctor.qualification)
                    titleName.append(" ")
                }
                if (doctor.specialty != null) {
                    titleName.append(doctor.specialty)
                }

                Text(
                    text = doctor.name!!,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (titleName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = titleName.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Grey_500
                    )
                }

                if (doctor.phoneNumber != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Call, contentDescription = null
                        )

                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = doctor.phoneNumber!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Grey_500
                        )
                    }
                }

            }

        }
    }
}

@Composable
fun DashedDivider() {
//    Canvas(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(1.dp)
//    ) {
//        val dashWidth = 10f
//        val dashGap = 5f
//        val startX = 0f
//        val endX = size.width
//        val y = size.height / 2
//
//        var currentX = startX
//        val paint = android.graphics.Paint().apply {
//            color = Grey_500.hashCode()
//            strokeWidth = size.height
//            isAntiAlias = true
//        }
//
//        while (currentX < endX) {
//            drawLine(
//                color = Grey_500,
//                start = androidx.compose.ui.geometry.Offset(currentX, y),
//                end = androidx.compose.ui.geometry.Offset(currentX + dashWidth, y),
//                strokeWidth = size.height
//            )
//            currentX += dashWidth + dashGap
//        }
//    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        repeat(30) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Grey_400)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun AppointmentDetailsSection(booking: MedicalConsultationHistory) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Date & Time
        ScheduleRow(
            icon = {
                Icon(
                    painter = rememberResourcePainter(R.drawable.ic_calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }, label = "Date:", value = booking.examinationDate?.toDMY() ?: "-"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScheduleRow(
            icon = {
                Icon(
                    painter = rememberResourcePainter(R.drawable.ic_time),
                    contentDescription = "Time",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = "Time:",
            value = "${booking.clinicSchedule.startTime.toHM()} - ${booking.clinicSchedule.endTime.toHM()}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visit Reason
        Text(
            text = "Visit Reason",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = booking.examinationReason ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = Grey_500
        )
    }
}

@Composable
fun ExaminationResultsSection(booking: MedicalConsultationHistory) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Examination Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (!booking.patientStatus.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Patient Status
            DetailRow(
                icon = {
                    Icon(
                        painter = rememberResourcePainter(R.drawable.ic_heart),
                        contentDescription = "Patient Status",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }, label = "Patient Status", value = booking.patientStatus ?: "-"
            )
        }

        if (!booking.diagnosis.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Diagnosis
            DetailRow(
                icon = {
                    Icon(
                        painter = rememberResourcePainter(R.drawable.ic_heart),
                        contentDescription = "Patient Status",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                label = "Diagnosis",
                value = booking.diagnosis ?: "Atrial Fibrillation",
            )
        }

        if (!booking.noteFromDoctor.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Notes from doctor
            DetailRow(
                icon = {
                    Icon(
                        painter = rememberResourcePainter(R.drawable.ic_heart),
                        contentDescription = "Patient Status",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                label = "Notes from doctor",
                value = booking.noteFromDoctor ?: "",
            )
        }
    }
}

@Composable
fun ReExaminationCalendarSection(booking: MedicalConsultationHistory) {
    // This is a placeholder. In a real app, this would display the actual re-examination schedule
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Re-Examination Calendar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // This would ideally come from the booking data
        // For now using dummy data as shown in the wireframe
        ScheduleRow(
            icon = {
                Icon(
                    painter = rememberResourcePainter(R.drawable.ic_calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }, label = "Date", value = "28 February"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScheduleRow(
            icon = {
                Icon(
                    painter = rememberResourcePainter(R.drawable.ic_time),
                    contentDescription = "Date",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }, label = "Time", value = "09:30 - 10:00 AM"
        )
    }
}

@Composable
fun DetailRow(
    icon: @Composable (() -> Unit)? = null, label: String, value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        // Label row
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            if (icon != null) {
                Box(modifier = Modifier.width(24.dp)) {
                    icon()
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value, style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ScheduleRow(
    icon: @Composable (() -> Unit)? = null, label: String, value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(modifier = Modifier.wrapContentSize()) {
            if (icon != null) {
                Box(modifier = Modifier.width(24.dp)) {
                    icon()
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value, style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun rememberResourcePainter(resId: Int) = painterResource(id = resId) 