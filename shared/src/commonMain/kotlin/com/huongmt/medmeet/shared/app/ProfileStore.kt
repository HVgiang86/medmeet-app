package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val user: User? = null,
    val currentChatServer: String = WholeApp.CHAT_BASE_URL,
    val currentBackendServer: String = WholeApp.BACKEND_URL
) : Store.State(isLoading)

sealed interface ProfileAction : Store.Action {
    data class GetUserSuccess(val user: User) : ProfileAction
    data object GetUser : ProfileAction
    data class Error(val error: Throwable) : ProfileAction
    data object Logout : ProfileAction

    data object NavigateUpdateProfile : ProfileAction
    data object NavigateHealthRecord : ProfileAction
    data object NavigateExaminationHistory : ProfileAction

    data object DismissError : ProfileAction

    data class ChangeChatServer(
        val server: String
    ) : ProfileAction

    data class ChangeBackendServer(
        val server: String
    ) : ProfileAction
}

sealed class ProfileEffect : Store.Effect {
    data object NavigateUpdateProfile : ProfileEffect()
    data object NavigateHealthRecord : ProfileEffect()
    data object NavigateExaminationHistory : ProfileEffect()
    data object Logout : ProfileEffect()
}

class ProfileStore(private val userRepository: UserRepository, private val tokenRepository: TokenRepository) :
    Store<ProfileState, ProfileAction, ProfileEffect>(
        initialState = ProfileState()
    ) {

    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(ProfileAction.Error(it))
        }

    override fun dispatch(oldState: ProfileState, action: ProfileAction) {
        when (action) {
            is ProfileAction.Error -> {
                setState(oldState.copy(isLoading = false, error = action.error))
            }

            ProfileAction.GetUser -> {
                setState(oldState.copy(isLoading = true))
                getUser()
            }

            is ProfileAction.GetUserSuccess -> {
                setState(oldState.copy(user = action.user, isLoading = false, error = null))
            }

            ProfileAction.DismissError -> {
                setState(oldState.copy(error = null))
            }

            ProfileAction.Logout -> {
                logout()
                setEffect(ProfileEffect.Logout)
            }

            ProfileAction.NavigateUpdateProfile -> {
                setEffect(ProfileEffect.NavigateUpdateProfile)
            }

            ProfileAction.NavigateExaminationHistory -> {
                setEffect(ProfileEffect.NavigateExaminationHistory)
            }
            ProfileAction.NavigateHealthRecord -> {
                setEffect(ProfileEffect.NavigateHealthRecord)
            }

            is ProfileAction.ChangeChatServer -> {
                changeChatServer(action.server)
                setState(oldState.copy(currentChatServer = action.server))
            }

            is ProfileAction.ChangeBackendServer -> {
                changeBackendServer(action.server)
                setState(oldState.copy(currentBackendServer = action.server))
            }
        }
    }

    private fun getUser() {
        launch {
            userRepository.getMyProfile().collect {
                userRepository.saveLocalUserId(it.id)
                WholeApp.USER = it
                sendAction(ProfileAction.GetUserSuccess(it))
            }
        }
    }

    private fun logout() {
        launch {
            tokenRepository.clearTokens()
        }
    }

    private fun changeChatServer(server: String) {
        WholeApp.CHAT_BASE_URL = server
        runFlow {
            userRepository.setChatBaseUrl(server)
        }
    }

    private fun changeBackendServer(server: String) {
        WholeApp.BACKEND_URL = server
        runFlow {
            userRepository.setBackendUrl(server)
        }
    }
}
