package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.base.toValidationException
import com.huongmt.medmeet.shared.core.WholeApp
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
    val error: Throwable? = null,

    val validateError: Throwable? = null,

    val bookingSuccessId: String? = null,
    val bookingFailedError: Throwable? = null
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

    data class Init(val clinicId: String) : BookingAction()

    data object CancelBooking : BookingAction()

    data class Error(val throwable: Throwable) : BookingAction()
    data object ClearError : BookingAction()

    data class UpdatePaymentMethod(val paymentMethod: PaymentMethod) : BookingAction()

    data object ConfirmBooking : BookingAction()
    data class BookingSuccess(val bookingId: String) : BookingAction()
    data class BookingFailed(val error: Throwable) : BookingAction()

    data object ClearBookingFailedError : BookingAction()

    data class ViewBookingDetails(
        val bookingId: String
    ) : BookingAction()

    data object ReturnHome : BookingAction()

    data class ValidateError(
        val error: Throwable
    ) : BookingAction()

    data object ClearValidateError : BookingAction()
}

sealed class BookingEffect : Store.Effect {
    data object CancelBooking : BookingEffect()
    data class ViewBookingDetails(
        val bookingId: String
    ) : BookingEffect()
    data object ReturnHome : BookingEffect()
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
        println("Booking Info: $oldState")

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
                        selectServiceState = BookingStep.SelectService(),
                        confirmationState = BookingStep.Confirmation(),
                        bookingSuccessId = null,
                        bookingFailedError = null
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
                    id = action.user.id,
                    name = action.user.name,
                    email = action.user.email,
                    gender = action.user.gender,
                    dateOfBirth = action.user.birthday,
                    province = action.user.province,
                    district = action.user.district,
                    commune = action.user.commune,
                    address = action.user.address,
                    phoneNumber = action.user.phoneNumber
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
                        if (oldState.inputPatientInfoState.patientInfo == null) {
                            sendAction(BookingAction.ValidateError(Throwable("Vui lòng nhập thông tin bệnh nhân")))
                            return
                        }

                        if (!validatePatientInfo(oldState.inputPatientInfoState.patientInfo!!)) {
                            return
                        }

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
                        if (oldState.selectScheduleState.clinicSchedule == null) {
                            sendAction(BookingAction.ValidateError("Vui lòng chọn lịch khám".toValidationException()))
                            return
                        }

                        setState(
                            oldState.copy(
                                currentStep = BookingStepType.CONFIRMATION,
                                confirmationState = BookingStep.Confirmation(
                                    bookingDetails = BookingDetails(
                                        patientInfo = oldState.inputPatientInfoState.patientInfo?.copy(id = WholeApp.USER_ID),
                                        clinic = oldState.clinic,
                                        medicalService = oldState.selectServiceState.selectedService,
                                        examinationDate = oldState.selectScheduleState.date,
                                        clinicSchedule = oldState.selectScheduleState.clinicSchedule,
                                        paymentMethod = PaymentMethod.CASH
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

            is BookingAction.BookingSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        bookingSuccessId = action.bookingId,
                        bookingFailedError = null
                    )
                )
            }

            BookingAction.ConfirmBooking -> {
                if (oldState.confirmationState.bookingDetails == null) {
                    return
                }

                println("Booking Details Confirm: ${oldState.confirmationState.bookingDetails.paymentMethod}")

                setState(
                    oldState.copy(
                        isLoading = true,
                        error = null,
                        bookingSuccessId = null,
                        bookingFailedError = null
                    )
                )
                doBooking(oldState.confirmationState.bookingDetails)
            }

            is BookingAction.BookingFailed -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        bookingFailedError = action.error
                    )
                )
            }

            BookingAction.ClearBookingFailedError -> {
                setState(
                    oldState.copy(
                        bookingFailedError = null
                    )
                )
            }

            BookingAction.ReturnHome -> {
                setEffect(BookingEffect.ReturnHome)
            }
            is BookingAction.ViewBookingDetails -> {
                setEffect(
                    BookingEffect.ViewBookingDetails(
                        bookingId = action.bookingId
                    )
                )
            }

            BookingAction.ClearValidateError -> {
                setState(
                    oldState.copy(
                        validateError = null
                    )
                )
            }
            is BookingAction.ValidateError -> {
                setState(
                    oldState.copy(
                        validateError = action.error
                    )
                )
            }
        }
    }

    private fun validatePatientInfo(
        patientInfo: PatientInfo
    ): Boolean {
        return when {
            patientInfo.name.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Họ tên không được để trống".toValidationException()))
                false
            }

            patientInfo.dateOfBirth == null -> {
                sendAction(BookingAction.ValidateError("Ngày sinh không được để trống".toValidationException()))
                false
            }

            patientInfo
                .province.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Tỉnh/Thành phố không được để trống".toValidationException()))
                false
            }
            patientInfo
                .district.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Quận/Huyện không được để trống".toValidationException()))
                false
            }
            patientInfo
                .commune.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Xã/Phường không được để trống".toValidationException()))

                false
            }
            patientInfo
                .address.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Địa chỉ không được để trống".toValidationException()))
                false
            }

            patientInfo.phoneNumber.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Số điện thoại không được để trống".toValidationException()))
                false
            }

            patientInfo.phoneNumber.length < 10 -> {
                sendAction(BookingAction.ValidateError("Số điện thoại không hợp lệ".toValidationException()))
                false
            }

            patientInfo.examinationReason.isNullOrEmpty() -> {
                sendAction(BookingAction.ValidateError("Lý do thăm khám không được để trống".toValidationException()))
                false
            }

            else -> true
        }
    }

    private fun doBooking(bookingDetails: BookingDetails) {
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(BookingAction.BookingFailed(it))
            }
        ) {
            bookingRepository.booking(
                bookingDetails = bookingDetails
            ).collect {
                sendAction(BookingAction.BookingSuccess(it))
            }
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
