package com.huongmt.medmeet.ui.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.shared.app.booking.BookingAction
import com.huongmt.medmeet.shared.app.booking.BookingEffect
import com.huongmt.medmeet.shared.app.booking.BookingStep
import com.huongmt.medmeet.shared.app.booking.BookingStepType
import com.huongmt.medmeet.shared.app.booking.BookingStore
import com.huongmt.medmeet.shared.core.entity.Clinic
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun BookingScreen(
    store: BookingStore,
    clinic: Clinic,
    onBack: () -> Unit = {}
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
        when(effect) {
            BookingEffect.CancelBooking -> {
                onBack()
            }
            null -> {}
        }
    }

    if (state.error != null) {
        Napier.d("Error: ${state.error}")
        ErrorDialog(throwable = state.error!!, onDismissRequest = {
            store.sendAction(BookingAction.ClearError)
        })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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

                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }
}
