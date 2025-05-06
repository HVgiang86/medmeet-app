package com.huongmt.medmeet.shared.app.booking

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.BookingDetails
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.core.entity.MedicalService
import com.huongmt.medmeet.shared.core.entity.PatientInfo
import com.huongmt.medmeet.shared.core.entity.PaymentMethod
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.BookingRepository
import com.huongmt.medmeet.shared.core.repository.ClinicRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import com.huongmt.medmeet.shared.utils.ext.nowDate
import kotlinx.datetime.LocalDate

enum class BookingStepType {
    SELECT_SERVICE, INPUT_PATIENT_INFO, SELECT_SCHEDULE, CONFIRMATION
}

sealed class BookingStep {
    data class SelectService(
        val listService: List<MedicalService>? = emptyList(),
        val selectedService: MedicalService? = null
    ) : BookingStep()

    data class InputPatientInfo(
        val patientInfo: PatientInfo? = null
    ) : BookingStep()

    data class SelectSchedule(
        val date: LocalDate? = null,
        val clinicSchedule: ClinicSchedule? = null,
        val availableSchedule: List<ClinicSchedule>? = emptyList()
    ) : BookingStep()

    data class Confirmation(
        val bookingDetails: BookingDetails? = null
    ) : BookingStep()
}

data class BookingState(
    val selectServiceState: BookingStep.SelectService = BookingStep.SelectService(),
    val inputPatientInfoState: BookingStep.InputPatientInfo = BookingStep.InputPatientInfo(),
    val selectScheduleState: BookingStep.SelectSchedule = BookingStep.SelectSchedule(),
    val confirmationState: BookingStep.Confirmation = BookingStep.Confirmation(),

    val currentStep: BookingStepType = BookingStepType.SELECT_SERVICE,
    val clinic: Clinic? = null,

    val isLoading: Boolean = false,
    val error: Throwable? = null
) : Store.State(isLoading)

sealed class BookingAction : Store.Action {
    data class NextStep(val currentStep: BookingStepType, val destinationStep: BookingStepType) :
        BookingAction()

    data class PreviousStep(
        val currentStep: BookingStepType,
        val destinationStep: BookingStepType
    ) : BookingAction()

    data class LoadMedicalServices(val clinicId: String) : BookingAction()
    data class LoadMedicalServicesSuccess(val services: List<MedicalService>) : BookingAction()

    data object LoadUserProfile : BookingAction()
    data class LoadUserProfileSuccess(val user: User) : BookingAction()

    data class LoadAvailableSchedule(
        val medicalServiceId: String,
        val date: LocalDate
    ) : BookingAction()

    data class LoadAvailableScheduleSuccess(
        val schedule: List<ClinicSchedule>
    ) : BookingAction()

    data class LoadClinicInfo(
        val clinicId: String
    ) : BookingAction()

    data class LoadClinicInfoSuccess(val clinic: Clinic) : BookingAction()

    data class SelectMedicalService(val service: MedicalService) : BookingAction()

    data class UpdatePatientInfo(val patientInfo: PatientInfo) : BookingAction()

    data class SelectDate(val date: LocalDate) : BookingAction()
    data class SelectSchedule(val schedule: ClinicSchedule) : BookingAction()

    data class SubmitBooking(
        val clinicId: String,
        val clinicName: String
    ) : BookingAction()

    data class Init(val clinicId: String) : BookingAction()

    data object CancelBooking : BookingAction()

    data class Error(val throwable: Throwable) : BookingAction()
    data object ClearError : BookingAction()

    data class UpdatePaymentMethod(val paymentMethod: PaymentMethod) : BookingAction()

    data object ConfirmBooking : BookingAction()
    data object BookingSuccess : BookingAction()
}

sealed class BookingEffect : Store.Effect {
    data object CancelBooking : BookingEffect()
}

class BookingStore(
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    private val clinicRepository: ClinicRepository
) : Store<BookingState, BookingAction, BookingEffect>(BookingState()) {

    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(BookingAction.Error(it))
        }

    override fun dispatch(oldState: BookingState, action: BookingAction) {
        when (action) {
            BookingAction.ClearError -> {
                setState(oldState.copy(error = null, isLoading = false))
            }

            is BookingAction.Error -> {
                setState(oldState.copy(error = action.throwable, isLoading = false))
            }

            is BookingAction.Init -> {
                setState(
                    oldState.copy(
                        isLoading = true,
                        currentStep = BookingStepType.SELECT_SERVICE,
                        error = null,
                        clinic = null,
                        selectScheduleState = BookingStep.SelectSchedule(),
                        inputPatientInfoState = BookingStep.InputPatientInfo(),
                        selectServiceState = BookingStep.SelectService()
                    )
                )

                sendAction(BookingAction.LoadClinicInfo(action.clinicId))
                sendAction(BookingAction.LoadMedicalServices(action.clinicId))
            }

            is BookingAction.LoadAvailableSchedule -> {
                setState(oldState.copy(isLoading = true))
                loadingAvailableSchedule(
                    action.medicalServiceId,
                    action.date
                )
            }

            is BookingAction.LoadAvailableScheduleSuccess -> {
                setState(
                    oldState.copy(
                        selectScheduleState = oldState.selectScheduleState.copy(
                            availableSchedule = action.schedule
                        ),
                        isLoading = false
                    )
                )
            }

            is BookingAction.LoadMedicalServices -> {
                setState(oldState.copy(isLoading = true))
                loadingMedicalServices(
                    action.clinicId
                )
            }

            is BookingAction.LoadMedicalServicesSuccess -> {
                setState(
                    oldState.copy(
                        selectServiceState = oldState.selectServiceState.copy(
                            listService = action.services
                        ),
                        isLoading = false
                    )
                )
            }

            BookingAction.LoadUserProfile -> {
                setState(oldState.copy(isLoading = true))
                loadingUserProfile()
            }

            is BookingAction.LoadUserProfileSuccess -> {
                val patientInfo = PatientInfo(
                    name = action.user.name,
                    email = action.user.email,
                    gender = action.user.gender,
                    dateOfBirth = action.user.birthday,
                    province = action.user.province,
                    district = action.user.district,
                    commune = action.user.commune,
                    address = action.user.address
                )
                setState(
                    oldState.copy(
                        inputPatientInfoState = oldState.inputPatientInfoState.copy(
                            patientInfo = patientInfo
                        ),
                        isLoading = false
                    )
                )
            }

            is BookingAction.SelectDate -> {
                setState(
                    oldState.copy(
                        selectScheduleState = oldState.selectScheduleState.copy(
                            date = action.date
                        )
                    )
                )

                sendAction(
                    BookingAction.LoadAvailableSchedule(
                        oldState.selectServiceState.selectedService?.id ?: "",
                        action.date
                    )
                )
            }

            is BookingAction.SelectMedicalService -> {
                setState(
                    oldState.copy(
                        selectServiceState = oldState.selectServiceState.copy(
                            selectedService = action.service
                        )
                    )
                )
            }

            is BookingAction.SelectSchedule -> {
                setState(
                    oldState.copy(
                        selectScheduleState = oldState.selectScheduleState.copy(
                            clinicSchedule = action.schedule
                        )
                    )
                )
            }

            is BookingAction.SubmitBooking -> {
            }

            is BookingAction.UpdatePatientInfo -> {
                setState(
                    oldState.copy(
                        inputPatientInfoState = oldState.inputPatientInfoState.copy(
                            patientInfo = action.patientInfo
                        )
                    )
                )
            }

            is BookingAction.NextStep -> {
                when (action.destinationStep) {
                    BookingStepType.SELECT_SERVICE -> {}
                    BookingStepType.INPUT_PATIENT_INFO -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.INPUT_PATIENT_INFO,
                                inputPatientInfoState = BookingStep.InputPatientInfo(),
                                selectScheduleState = BookingStep.SelectSchedule(),
                                confirmationState = BookingStep.Confirmation()
                            )
                        )

                        sendAction(BookingAction.LoadUserProfile)
                    }

                    BookingStepType.SELECT_SCHEDULE -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.SELECT_SCHEDULE,
                                selectScheduleState = BookingStep.SelectSchedule(),
                                confirmationState = BookingStep.Confirmation()
                            )
                        )

                        val now = nowDate()

                        sendAction(
                            BookingAction.LoadAvailableSchedule(
                                oldState.selectServiceState.selectedService?.id ?: "",
                                now
                            )
                        )
                    }

                    BookingStepType.CONFIRMATION -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.CONFIRMATION,
                                confirmationState = BookingStep.Confirmation(
                                    bookingDetails = BookingDetails(
                                        patientInfo = oldState.inputPatientInfoState.patientInfo,
                                        clinic = oldState.clinic,
                                        medicalService = oldState.selectServiceState.selectedService,
                                        examinationDate = oldState.selectScheduleState.date,
                                        clinicSchedule = oldState.selectScheduleState.clinicSchedule,
                                        clinicName = "",
                                        paymentMethod = null
                                    )
                                )
                            )
                        )
                    }
                }
            }

            is BookingAction.PreviousStep -> {
                when (action.destinationStep) {
                    BookingStepType.SELECT_SERVICE -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.SELECT_SERVICE
                            )
                        )
                    }

                    BookingStepType.INPUT_PATIENT_INFO -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.INPUT_PATIENT_INFO
                            )
                        )
                    }

                    BookingStepType.SELECT_SCHEDULE -> {
                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.SELECT_SCHEDULE
                            )
                        )
                    }

                    BookingStepType.CONFIRMATION -> {}
                }
            }

            is BookingAction.CancelBooking -> {
                setEffect(BookingEffect.CancelBooking)
            }

            is BookingAction.LoadClinicInfo -> {
                getClinicInfo(action.clinicId)
            }

            is BookingAction.LoadClinicInfoSuccess -> {
                setState(oldState.copy(clinic = action.clinic))
            }

            is BookingAction.UpdatePaymentMethod -> {
                setState(
                    oldState.copy(
                        confirmationState = oldState.confirmationState.copy(
                            bookingDetails = oldState.confirmationState.bookingDetails?.copy(
                                paymentMethod = action.paymentMethod
                            )
                        )
                    )
                )
            }

            BookingAction.BookingSuccess -> TODO()
            BookingAction.ConfirmBooking -> TODO()
        }
    }

    private fun loadingMedicalServices(
        clinicId: String
    ) {
        runFlow {
            bookingRepository.getMedicalServices(clinicId).collect {
                sendAction(
                    BookingAction.LoadMedicalServicesSuccess(it)
                )
            }
        }
    }

    private fun getClinicInfo(clinicId: String) {
        runFlow {
            clinicRepository.getClinicById(clinicId).collect {
                sendAction(
                    BookingAction.LoadClinicInfoSuccess(it)
                )
            }
        }
    }

    private fun loadingUserProfile() {
        runFlow {
            userRepository.getMyProfile().collect {
                sendAction(
                    BookingAction.LoadUserProfileSuccess(it)
                )
            }
        }
    }

    private fun loadingAvailableSchedule(
        medicalServiceId: String,
        date: LocalDate
    ) {
        runFlow {
            bookingRepository.getAvailableTimeSlots(medicalServiceId, date).collect {
                sendAction(
                    BookingAction.LoadAvailableScheduleSuccess(it)
                )
            }
        }
    }
}
