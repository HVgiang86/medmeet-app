package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.ClinicRepository
import com.huongmt.medmeet.shared.core.repository.MedicalRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate

data class HomeState(
    val isLoading: Boolean = false,
    val isClinicLoading: Boolean = false,
    val user: User? = null,
    val error: Throwable? = null,
    val clinics: List<Clinic> = emptyList(),
    val appointments: List<MedicalConsultationHistory> = emptyList(),
    val displayAppointments: MedicalConsultationHistory? = null
) : Store.State(loading = isLoading)

sealed class HomeAction : Store.Action {
    object LoadUser : HomeAction()
    object LoadClinics : HomeAction()
    data class LoadClinicSuccess(val clinics: List<Clinic>) : HomeAction()
    data class LoadUserSuccess(val user: User) : HomeAction()

    data class LoadAppointments(val selectedDate: LocalDate? = null) : HomeAction()
    data class LoadAppointmentsSuccess(
        val selectedDate: LocalDate? = null,
        val appointments: List<MedicalConsultationHistory>
    ) : HomeAction()

    data class ShowAppointmentDay(val date: LocalDate) : HomeAction()

    data class ShowError(val error: Throwable) : HomeAction()
    data object DismissError : HomeAction()
}

sealed class HomeEffect : Store.Effect {
    data class NavigateToClinicDetail(val clinic: Clinic) : HomeEffect()
}

class HomeStore(
    private val userRepository: UserRepository,
    private val clinicRepository: ClinicRepository,
    private val medicalRepository: MedicalRepository
) : Store<HomeState, HomeAction, HomeEffect>(
    initialState = HomeState()
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(HomeAction.ShowError(it))
        }

    override fun dispatch(oldState: HomeState, action: HomeAction) {
        when (action) {
            HomeAction.DismissError -> {
                setState(oldState.copy(isLoading = false, error = null))
            }

            HomeAction.LoadClinics -> {
                setState(oldState.copy(isClinicLoading = true))
                loadClinics()
            }

            HomeAction.LoadUser -> {
                setState(oldState.copy(isLoading = true))
                getProfile()
            }

            is HomeAction.ShowError -> {
                setState(oldState.copy(isLoading = false, error = action.error))
            }

            is HomeAction.LoadClinicSuccess -> {
                setState(oldState.copy(isClinicLoading = false, clinics = action.clinics, error = null))
            }

            is HomeAction.LoadUserSuccess -> {
                setState(oldState.copy(isLoading = false, user = action.user, error = null))
            }

            is HomeAction.LoadAppointments -> {
                loadAppointments(action.selectedDate)
            }

            is HomeAction.LoadAppointmentsSuccess -> {
                val displayAppointments = action.appointments.filter {
                    it.status == MedicalRecordStatus.PENDING
                }

                setState(
                    oldState.copy(
                        isLoading = false,
                        appointments = displayAppointments,
                        error = null
                    )
                )

                if (action.selectedDate != null) sendAction(HomeAction.ShowAppointmentDay(action.selectedDate))
            }

            is HomeAction.ShowAppointmentDay -> {
                val displayAppointments = oldState.appointments.filter {
                    it.examinationDate == action.date
                }
                setState(
                    oldState.copy(
                        displayAppointments = displayAppointments.firstOrNull()
                    )
                )
            }
        }
    }

    private fun getProfile() {
        runFlow(exception = coroutineExceptionHandler {}) {
            userRepository.getMyProfile().collect {
                Napier.d("Profile: $it")
                WholeApp.USER_ID = it.id
                sendAction(HomeAction.LoadUserSuccess(it))
            }
        }
    }

    private fun loadClinics() {
        runFlow {
            Napier.d("Load clinics")
            clinicRepository.getAllClinics(page = 1, pageSize = 10).collect { result ->
                Napier.d("Clinics: $result")
                if (result.isNotEmpty()) {
                    sendAction(HomeAction.LoadClinicSuccess(result))
                } else {
                    sendAction(HomeAction.ShowError(Throwable("No clinics found")))
                }
            }
        }
    }

    fun navigateToClinicDetail(clinic: Clinic) {
        setEffect(HomeEffect.NavigateToClinicDetail(clinic))
    }

    private fun loadAppointments(date: LocalDate?) {
        runFlow(exception = coroutineExceptionHandler {}) {
            medicalRepository.getMedicalConsultations().collect { appointments ->
                Napier.d("Appointments: $appointments")
                sendAction(
                    HomeAction.LoadAppointmentsSuccess(
                        appointments = appointments,
                        selectedDate = date
                    )
                )
            }
        }
    }
}
