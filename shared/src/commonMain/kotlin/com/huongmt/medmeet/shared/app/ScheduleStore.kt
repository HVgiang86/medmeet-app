package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.repository.MedicalRepository
import io.github.aakira.napier.Napier

data class ScheduleState(
    val isLoading: Boolean = false,
    val appointments: List<MedicalConsultationHistory> = emptyList(),
    val displayAppointments: List<MedicalConsultationHistory> = emptyList(),
    val error: Throwable? = null,
    val selectedTab: ScheduleTab = ScheduleTab.UPCOMING,
    val numberOfPending: Int = 0,
    val numberOfCompleted: Int = 0,
    val numberOfCanceled: Int = 0
) : Store.State(loading = isLoading)

enum class ScheduleTab {
    UPCOMING, COMPLETED, CANCELED
}

sealed class ScheduleAction : Store.Action {
    data class LoadAppointments(val showTab: ScheduleTab? = null) : ScheduleAction()
    data class LoadAppointmentsSuccess(
        val appointments: List<MedicalConsultationHistory>,
        val showTab: ScheduleTab?
    ) : ScheduleAction()

    data class ShowError(val error: Throwable) : ScheduleAction()
    data object DismissError : ScheduleAction()

    data class SelectTab(val tab: ScheduleTab) : ScheduleAction()
    data object NavigateBack : ScheduleAction()
}

sealed class ScheduleEffect : Store.Effect {
    data object NavigateBack : ScheduleEffect()
}

class ScheduleStore(
    private val medicalRepository: MedicalRepository
) : Store<ScheduleState, ScheduleAction, ScheduleEffect>(
    initialState = ScheduleState()
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(ScheduleAction.ShowError(it))
        }

    override fun dispatch(oldState: ScheduleState, action: ScheduleAction) {
        when (action) {
            is ScheduleAction.LoadAppointments -> {
                setState(
                    oldState.copy(
                        isLoading = true
                    )
                )
                loadAppointments(action.showTab)
            }

            is ScheduleAction.LoadAppointmentsSuccess -> {
                val pendingCount =
                    action.appointments.count { it.status == MedicalRecordStatus.PENDING }
                val completedCount =
                    action.appointments.count { it.status == MedicalRecordStatus.COMPLETED }
                val canceledCount =
                    action.appointments.count { it.status == MedicalRecordStatus.CANCELED }

                val newState = oldState.copy(
                    numberOfPending = pendingCount,
                    numberOfCompleted = completedCount,
                    numberOfCanceled = canceledCount
                )
                if (action.showTab != null) {
                    val showAppointments = filterAppointments(action.appointments, action.showTab)
                    setState(
                        newState.copy(
                            isLoading = false,
                            displayAppointments = showAppointments,
                            appointments = action.appointments,
                            selectedTab = action.showTab
                        )
                    )
                } else {
                    setState(
                        newState.copy(
                            isLoading = false,
                            appointments = action.appointments
                        )
                    )
                }
            }

            is ScheduleAction.ShowError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = action.error
                    )
                )
            }

            is ScheduleAction.DismissError -> {
                setState(
                    oldState.copy(
                        error = null
                    )
                )
            }

            is ScheduleAction.SelectTab -> {
                val showAppointments = filterAppointments(oldState.appointments, action.tab)
                setState(
                    oldState.copy(
                        selectedTab = action.tab,
                        displayAppointments = showAppointments
                    )
                )
            }

            is ScheduleAction.NavigateBack -> {
                setEffect(ScheduleEffect.NavigateBack)
            }
        }
    }

    private fun filterAppointments(
        appointments: List<MedicalConsultationHistory>,
        showTab: ScheduleTab
    ): List<MedicalConsultationHistory> {
        return appointments.filter {
            when (showTab) {
                ScheduleTab.UPCOMING -> it.status == MedicalRecordStatus.PENDING
                ScheduleTab.COMPLETED -> it.status == MedicalRecordStatus.COMPLETED
                ScheduleTab.CANCELED -> it.status == MedicalRecordStatus.CANCELED
            }
        }
    }

    private fun loadAppointments(showTab: ScheduleTab?) {
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(ScheduleAction.NavigateBack)
            }
        ) {
            medicalRepository.getMedicalConsultations().collect { appointments ->
                Napier.d("Appointments: $appointments")
                sendAction(ScheduleAction.LoadAppointmentsSuccess(appointments, showTab))
            }
        }
    }
}
