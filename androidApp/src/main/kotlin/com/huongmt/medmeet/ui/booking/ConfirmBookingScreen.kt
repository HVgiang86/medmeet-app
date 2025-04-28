package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.huongmt.medmeet.R
import com.huongmt.medmeet.shared.app.booking.BookingStep
import com.huongmt.medmeet.shared.app.booking.BookingStore
import com.huongmt.medmeet.shared.core.entity.BookingDetails
import com.huongmt.medmeet.theme.Grey_200
import com.huongmt.medmeet.utils.ext.toHM

@Composable
fun ConfirmBookingScreen(
    store: BookingStore, state: BookingStep.Confirmation
) {

    ConfirmBookingContent(
        store = store, state = state
    )
}


@Composable
private fun ConfirmBookingContent(
    store: BookingStore, state: BookingStep.Confirmation
) {
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
                text = "Confirm Health Booking",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.bookingDetails != null) {
                BookingInfoSection(
                    bookingDetails = state.bookingDetails!!,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Grey_200)

//            PatientInfoSection(bookingDetails.patient)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Grey_200)

            // Price information
            PriceSection(state.bookingDetails?.medicalService?.currentPrice ?: 0L)

            Button(
                onClick = {

                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Booking")
            }
        }
    }
}

@Composable
private fun BookingInfoSection(
    bookingDetails: BookingDetails
) {
    val clinic = bookingDetails.clinic
    Text(
        text = bookingDetails.medicalService?.name ?: "",
        style = MaterialTheme.typography.titleLarge,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = clinic?.name ?: "",
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Clinic address
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
        )

        Text(
            text = clinic?.address ?: "",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Examination Date: ${bookingDetails.examinationDate}",
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Time: ${bookingDetails.clinicSchedule?.startTime?.toHM()}",
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )

}

@Composable
private fun PriceSection(price: Long) {
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
                text = "$price",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

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
                text = "$price",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}