package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.core.repository.ClinicRepository
import com.huongmt.medmeet.shared.utils.ext.to2DigitString
import io.github.aakira.napier.Napier

data class ClinicDetailState(
    val isLoading: Boolean = true,
    val clinic: Clinic? = null,
    val error: Throwable? = null,
    val clinicScheduleDisplay: String? = null
) : Store.State(loading = isLoading)

sealed class ClinicDetailAction : Store.Action {
    data class LoadClinic(val clinicId: String) : ClinicDetailAction()
    data class LoadClinicSuccess(val clinic: Clinic) : ClinicDetailAction()
    data class ShowError(val error: Throwable) : ClinicDetailAction()
    data object DismissError : ClinicDetailAction()
    data class GetClinicSchedule(val clinicId: String) : ClinicDetailAction()
    data class GetClinicScheduleSuccess(val schedule: List<ClinicSchedule>) : ClinicDetailAction()
    data object LoadClinicError : ClinicDetailAction()
    data object NavigateBack : ClinicDetailAction()
}

sealed class ClinicDetailEffect : Store.Effect {
    data object NavigateBack : ClinicDetailEffect()
    data object BookAppointment : ClinicDetailEffect()
}

class ClinicDetailStore(
    private val clinicRepository: ClinicRepository
) : Store<ClinicDetailState, ClinicDetailAction, ClinicDetailEffect>(
    initialState = ClinicDetailState()
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(ClinicDetailAction.ShowError(it))
        }

    override fun dispatch(oldState: ClinicDetailState, action: ClinicDetailAction) {
        when (action) {
            is ClinicDetailAction.LoadClinic -> {
                setState(oldState.copy(isLoading = true))
                loadClinic(action.clinicId)
            }

            is ClinicDetailAction.LoadClinicSuccess -> {
                Napier.d { "Clinic detail loaded: ${action.clinic}" }
                setState(
                    oldState.copy(
                        isLoading = false,
                        clinic = action.clinic,
                        error = null
                    )
                )
            }

            is ClinicDetailAction.ShowError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = action.error
                    )
                )
            }

            ClinicDetailAction.DismissError -> {
                setState(oldState.copy(error = null))
            }

            is ClinicDetailAction.GetClinicSchedule -> {
                if (!oldState.loading) setState(oldState.copy(isLoading = true))

                getClinicSchedule(clinicId = action.clinicId)
            }

            is ClinicDetailAction.GetClinicScheduleSuccess -> {
                val startSchedule = action.schedule.sortedBy { it.startTime }.first()
                val endSchedule = action.schedule.sortedByDescending { it.endTime }.first()

                val displayText =
                    "T2 - T6, ${startSchedule.startTime.hour.to2DigitString()}:${startSchedule.startTime.minute.to2DigitString()} - ${endSchedule.endTime.hour.to2DigitString()}:${endSchedule.endTime.minute.to2DigitString()}"

                setState(
                    oldState.copy(
                        isLoading = false,
                        error = null,
                        clinicScheduleDisplay = displayText
                    )
                )
            }

            ClinicDetailAction.LoadClinicError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = null
                    )
                )
                setEffect(ClinicDetailEffect.NavigateBack)
            }

            ClinicDetailAction.NavigateBack -> {
                setEffect(ClinicDetailEffect.NavigateBack)
            }
        }
    }

    private fun getClinicSchedule(
        clinicId: String
    ) {
        runFlow(
            exception = coroutineExceptionHandler {
            }
        ) {
            clinicRepository.getClinicScheduleByClinicId(clinicId).collect { schedule ->
                Napier.d("Clinic schedule: $schedule")
                sendAction(ClinicDetailAction.GetClinicScheduleSuccess(schedule))
            }
        }
    }

    private fun loadClinic(clinicId: String) {
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(ClinicDetailAction.LoadClinicError)
            }
        ) {
            clinicRepository.getClinicById(clinicId).collect { clinic ->
                Napier.d("Clinic detail: $clinic")
                sendAction(ClinicDetailAction.LoadClinicSuccess(clinic))
            }
        }
    }

    fun bookAppointment() {
        setEffect(ClinicDetailEffect.BookAppointment)
    }
}
