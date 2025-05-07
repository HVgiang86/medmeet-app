package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.BookingRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import org.koin.core.component.KoinComponent

sealed class BookingDetailAction : Store.Action {
    data class LoadBookingDetail(val bookingId: String) : BookingDetailAction()
    data class ShowError(val error: Throwable) : BookingDetailAction()
    data class LoadBookingDetailSuccess(val bookingDetail: MedicalConsultationHistory) :
        BookingDetailAction()

    data class LoadDoctorDetail(val doctorId: String) : BookingDetailAction()
    data class LoadDoctorDetailSuccess(val doctor: User) : BookingDetailAction()

    data object ClearError : BookingDetailAction()

    data object GoBack : BookingDetailAction()
}

sealed class BookingDetailEffect : Store.Effect {
    data object NavigateBack : BookingDetailEffect()
}

data class BookingDetailState(
    val isLoading: Boolean = false,
    val bookingId: String = "",
    val detail: MedicalConsultationHistory? = null,
    val doctor: User? = null,
    val error: Throwable? = null
) : Store.State(isLoading)

class BookingDetailStore(
    private val bookingRepository: BookingRepository, private val userRepository: UserRepository
) : Store<BookingDetailState, BookingDetailAction, BookingDetailEffect>(BookingDetailState()),
    KoinComponent {

    override fun dispatch(oldState: BookingDetailState, action: BookingDetailAction) {
        when (action) {
            BookingDetailAction.ClearError -> {
                setState(
                    oldState.copy(
                        error = null
                    )
                )
            }

            is BookingDetailAction.LoadBookingDetail -> {
                if (!oldState.isLoading) {
                    setState(
                        oldState.copy(
                            isLoading = true, error = null
                        )
                    )
                }
                loadBookingDetail(action.bookingId)
            }

            is BookingDetailAction.LoadBookingDetailSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false, detail = action.bookingDetail
                    )
                )

                if (!action.bookingDetail.doctorId.isNullOrEmpty()) {
                    sendAction(
                        BookingDetailAction.LoadDoctorDetail(
                            action.bookingDetail.doctorId
                        )
                    )
                }
            }

            is BookingDetailAction.ShowError -> {
                setState(
                    oldState.copy(
                        error = action.error, isLoading = false
                    )
                )
            }

            BookingDetailAction.GoBack -> {
                setEffect(BookingDetailEffect.NavigateBack)
            }

            is BookingDetailAction.LoadDoctorDetail -> {
                loadDoctorDetail(action.doctorId)
            }

            is BookingDetailAction.LoadDoctorDetailSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false, doctor = action.doctor
                    )
                )
            }
        }
    }


    private fun loadBookingDetail(bookingId: String) {
        runFlow(exception = coroutineExceptionHandler {
            sendAction((BookingDetailAction.GoBack))
        }) {
            bookingRepository.getBookingDetail(bookingId).collect { result ->
                sendAction(BookingDetailAction.LoadBookingDetailSuccess(result))
            }
        }
    }

    private fun loadDoctorDetail(doctorId: String) {
        runFlow(exception = coroutineExceptionHandler {

        }) {
            userRepository.getProfileById(doctorId).collect { result ->
                sendAction(BookingDetailAction.LoadDoctorDetailSuccess(result))
            }
        }
    }

    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(BookingDetailAction.ShowError(it))
        }
}
