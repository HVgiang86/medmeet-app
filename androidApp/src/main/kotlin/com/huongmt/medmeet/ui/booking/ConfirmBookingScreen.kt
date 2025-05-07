package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.shared.app.BookingAction
import com.huongmt.medmeet.shared.app.BookingStep
import com.huongmt.medmeet.shared.app.BookingStepType
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.core.entity.BookingDetails
import com.huongmt.medmeet.shared.core.entity.PatientInfo
import com.huongmt.medmeet.shared.core.entity.PaymentMethod
import com.huongmt.medmeet.theme.Grey_200
import com.huongmt.medmeet.theme.icons.IC_BANK
import com.huongmt.medmeet.utils.ext.formatPrice
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toHM

@Composable
fun ConfirmBookingScreen(
    store: BookingStore, state: BookingStep.Confirmation,
) {
    ConfirmBookingContent(
        store = store, state = state
    )
}

@Composable
private fun ConfirmBookingContent(
    store: BookingStore, state: BookingStep.Confirmation,
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 60.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "Confirm Booking",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ticket-style card for booking details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header with ticket decoration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "APPOINTMENT TICKET",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Dashed line separator
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
                                    .background(Grey_200)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    if (state.bookingDetails != null) {
                        BookingInfoSection(
                            bookingDetails = state.bookingDetails!!,
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp), color = Grey_200
                    )

                    state.bookingDetails?.patientInfo?.let { patient ->
                        PatientInfoSection(patient)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp), color = Grey_200
                    )

                    // Price and payment method selection
                    PriceSection(price = state.bookingDetails?.medicalService?.currentPrice ?: 0L,
                        selectedPaymentMethod = state.bookingDetails?.paymentMethod
                            ?: PaymentMethod.CASH,
                        onPaymentMethodSelected = {
                            store.sendAction(BookingAction.UpdatePaymentMethod(paymentMethod = it))
                        })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
            ) {
                SecondaryButton(modifier = Modifier.weight(1f), onClick = {
                    store.sendAction(
                        BookingAction.PreviousStep(
                            currentStep = BookingStepType.CONFIRMATION,
                            destinationStep = BookingStepType.SELECT_SCHEDULE
                        )
                    )
                }, text = {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                })

                Spacer(modifier = Modifier.width(16.dp))

                PrimaryButton(modifier = Modifier.weight(1f), onClick = {
                    store.sendAction(BookingAction.ConfirmBooking)
                }, text = {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                })
            }
        }
    }

}

@Composable
private fun BookingInfoSection(
    bookingDetails: BookingDetails,
) {
    val clinic = bookingDetails.clinic

    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = bookingDetails.clinic?.logo ?: "",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = bookingDetails.medicalService?.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = clinic?.name ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Clinic address
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = clinic?.address ?: "",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Time and date in a highlighted section
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DATE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${bookingDetails.examinationDate?.toDMY()}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "TIME",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${bookingDetails.clinicSchedule?.startTime?.toHM()}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PatientInfoSection(patient: PatientInfo) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Patient Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Patient name
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))


            Text(
                text = patient.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        // Patient birth date
        if (patient.dateOfBirth != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = patient.dateOfBirth?.toDMY() ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Patient phone number

        if (!patient.phoneNumber.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            // Contact information
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = patient.phoneNumber ?: "", style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Patient address
        if (!patient.address.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                val address =
                    "${patient.address}, ${patient.commune}, ${patient.district}, ${patient.province}"

                Text(
                    text = address, style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (!patient.examinationReason.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reason for visit:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = patient.examinationReason ?: "", style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PriceSection(
    price: Long,
    selectedPaymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Payment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Consultation Fee", style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = price.toDouble().formatPrice(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment method selection
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Cash option
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onPaymentMethodSelected(PaymentMethod.CASH) }
            .border(
                width = 1.dp,
                color = if (selectedPaymentMethod == PaymentMethod.CASH) MaterialTheme.colorScheme.primary else Grey_200,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedPaymentMethod == PaymentMethod.CASH,
                onClick = { onPaymentMethodSelected(PaymentMethod.CASH) })

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Cash", style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // VNPAY option
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false) { onPaymentMethodSelected(PaymentMethod.VNPAY) }
            .border(
                width = 1.dp,
                color = if (selectedPaymentMethod == PaymentMethod.VNPAY) MaterialTheme.colorScheme.primary else Grey_200,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedPaymentMethod == PaymentMethod.VNPAY,
                enabled = false,
                onClick = { onPaymentMethodSelected(PaymentMethod.VNPAY) })

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "VNPAY", style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = IC_BANK,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = price.toDouble().formatPrice(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}