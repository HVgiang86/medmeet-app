package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.ClinicRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import io.github.aakira.napier.Napier

data class HomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: Throwable? = null,
    val clinics: List<Clinic> = emptyList()
) : Store.State(loading = isLoading)

sealed class HomeAction : Store.Action {
    object LoadUser : HomeAction()
    object LoadClinics : HomeAction()
    data class LoadClinicSuccess(val clinics: List<Clinic>) : HomeAction()
    data class LoadUserSuccess(val user: User) : HomeAction()
    data class ShowError(val error: Throwable) : HomeAction()
    data object DismissError : HomeAction()
}

sealed class HomeEffect : Store.Effect {
    data class NavigateToClinicDetail(val clinic: Clinic) : HomeEffect()
}

class HomeStore(
    private val userRepository: UserRepository,
    private val clinicRepository: ClinicRepository
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
                setState(oldState.copy(isLoading = false, clinics = action.clinics, error = null))
            }

            is HomeAction.LoadUserSuccess -> {
                setState(oldState.copy(isLoading = false, user = action.user, error = null))
            }
        }
    }

    private fun getProfile() {
        runFlow {
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
}
