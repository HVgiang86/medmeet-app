package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.component.MedicalServiceItem
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.shared.app.booking.BookingAction
import com.huongmt.medmeet.shared.app.booking.BookingStep
import com.huongmt.medmeet.shared.app.booking.BookingStepType
import com.huongmt.medmeet.shared.app.booking.BookingStore

@Composable
fun SelectServiceScreen(
    store: BookingStore,
    state: BookingStep.SelectService,
) {
    SelectServiceContent(
        store = store,
        state = state,
    )
}

@Composable
private fun SelectServiceContent(
    store: BookingStore,
    state: BookingStep.SelectService,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "Book Appointment",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            val list = state.listService

            if (list != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(list) { service ->
                        MedicalServiceItem(service = service,
                            selected = service.id == state.selectedService?.id,
                            onClick = {
                                store.sendAction(
                                    BookingAction.SelectMedicalService(service)
                                )
                            })
                    }

                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            SecondaryButton(modifier = Modifier.weight(1f), onClick = {
                store.sendAction(BookingAction.CancelBooking)
            }, text = {
                Text(
                    text = "Back", style = MaterialTheme.typography.labelLarge, color = Color.Black
                )
            })

            Spacer(modifier = Modifier.width(16.dp))

            PrimaryButton(modifier = Modifier.weight(1f), onClick = {
                if (state.selectedService == null) {
                    return@PrimaryButton
                }

                store.sendAction(
                    BookingAction.NextStep(
                        currentStep = BookingStepType.SELECT_SERVICE,
                        destinationStep = BookingStepType.INPUT_PATIENT_INFO
                    )
                )
            }, text = {
                Text(
                    text = "Next", style = MaterialTheme.typography.labelLarge, color = Color.White
                )
            })
        }
    }
}
