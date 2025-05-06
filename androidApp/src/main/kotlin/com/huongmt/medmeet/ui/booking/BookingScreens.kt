package com.huongmt.medmeet.ui.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.huongmt.medmeet.component.BaseNoticeDialog
import com.huongmt.medmeet.component.ButtonType
import com.huongmt.medmeet.component.DialogType
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.BookingAction
import com.huongmt.medmeet.shared.app.BookingEffect
import com.huongmt.medmeet.shared.app.BookingStepType
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.core.entity.Clinic
import io.github.aakira.napier.Napier

@Composable
fun BookingScreen(
    store: BookingStore, clinic: Clinic, onBack: () -> Unit = {}
) {

    BackHandler {
        onBack()
    }

    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        store.sendAction(BookingAction.Init(clinicId = clinic.id))
    }

    LaunchedEffect(effect) {
        when (effect) {
            BookingEffect.CancelBooking -> {
                onBack()
            }

            null -> {}
            BookingEffect.ReturnHome -> {
                onBack()
            }

            is BookingEffect.ViewBookingDetails -> {

            }
        }
    }

    if (state.error != null) {
        Napier.d("Error: ${state.error}")
        ErrorDialog(throwable = state.error!!, onDismissRequest = {
            store.sendAction(BookingAction.ClearError)
        })
    }

    if (state.bookingFailedError != null) {
        Napier.d("Booking Failed Error: ${state.bookingFailedError}")
        ErrorDialog(throwable = state.bookingFailedError!!, onDismissRequest = {
            store.sendAction(BookingAction.ClearBookingFailedError)
        })
    }

    if (state.bookingSuccessId != null) {
        BaseNoticeDialog(
            type = DialogType.SUCCESS,
            title = "Booking Success",
            text = "Your booking has been successfully completed.",
            buttonType = ButtonType.PairButton(primary = "View Detail",
                secondary = "Return Home",
                onPrimaryClick = {
                    store.sendAction(BookingAction.ViewBookingDetails(state.bookingSuccessId!!))
                },
                onSecondaryClick = {
                    store.sendAction(BookingAction.ReturnHome)
                }),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (state.currentStep) {
                BookingStepType.SELECT_SERVICE -> {
                    SelectServiceScreen(
                        store = store,
                        state = state.selectServiceState,
                    )
                }

                BookingStepType.INPUT_PATIENT_INFO -> {
                    PatientInfoScreen(
                        store = store,
                        state = state.inputPatientInfoState,
                    )
                }

                BookingStepType.SELECT_SCHEDULE -> {
                    SelectScheduleScreen(
                        store = store,
                        state = state.selectScheduleState,
                    )
                }

                BookingStepType.CONFIRMATION -> {
                    ConfirmBookingScreen(
                        store = store,
                        state = state.confirmationState,
                    )
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }
}
