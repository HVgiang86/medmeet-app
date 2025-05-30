package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.UpdateProfileData
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.UserRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

data class ProfileDetailState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val originalUser: User? = null,
    val avatarChanged: Boolean = false,
    val newAvatarUri: String? = null,
    val enableSaveBtn: Boolean = false
) : Store.State(isLoading)

sealed interface ProfileDetailAction : Store.Action {
    data object GetUser : ProfileDetailAction
    data class GetUserSuccess(val user: User) : ProfileDetailAction
    data class UpdateAvatar(val avatarUri: String) : ProfileDetailAction
    data class UpdateUser(val dateUpdate: UpdateProfileData) : ProfileDetailAction
    data object UpdateProfileSuccess : ProfileDetailAction
    data object UpdateAvatarSuccess : ProfileDetailAction
    data class Error(val error: Throwable) : ProfileDetailAction
    data object NavigateBack : ProfileDetailAction
    data object DismissError : ProfileDetailAction
}

sealed class ProfileDetailEffect : Store.Effect {
    data object NavigateBack : ProfileDetailEffect()
    data class ShowMessage(val message: String) : ProfileDetailEffect()
}

class ProfileDetailStore(private val userRepository: UserRepository) :
    Store<ProfileDetailState, ProfileDetailAction, ProfileDetailEffect>(
        initialState = ProfileDetailState()
    ) {

    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(ProfileDetailAction.Error(it))
        }

    override fun dispatch(oldState: ProfileDetailState, action: ProfileDetailAction) {
        when (action) {
            is ProfileDetailAction.Error -> {
                setState(oldState.copy(isLoading = false, error = action.error))
            }

            ProfileDetailAction.GetUser -> {
                setState(oldState.copy(isLoading = true, enableSaveBtn = false))
                getUser()
            }

            is ProfileDetailAction.GetUserSuccess -> {
                Napier.d { "Get user success: ${action.user}" }
                setState(
                    oldState.copy(
                        originalUser = action.user,
                        isLoading = false,
                        error = null
                    )
                )
            }

            ProfileDetailAction.DismissError -> {
                setState(oldState.copy(error = null))
            }

            ProfileDetailAction.NavigateBack -> {
                setEffect(ProfileDetailEffect.NavigateBack)
            }

            is ProfileDetailAction.UpdateUser -> {
                setState(oldState.copy(isLoading = true))
                updateUserProfile(action.dateUpdate)
            }

            is ProfileDetailAction.UpdateAvatar -> {
//                setState(oldState.copy(
//                    avatarChanged = true,
//                    newAvatarUri = action.avatarUri
//                ))
//                updateAvatar(action.avatarUri)
            }

            ProfileDetailAction.UpdateProfileSuccess -> {
                setEffect(ProfileDetailEffect.ShowMessage("Profile updated successfully"))
                setState(oldState.copy(isLoading = false))
                getUser()
            }

            ProfileDetailAction.UpdateAvatarSuccess -> {
//                setEffect(ProfileDetailEffect.ShowMessage("Avatar updated successfully"))
//                setState(oldState.copy(isLoading = false, avatarChanged = false, newAvatarUri = null))
//                getUser() // Refresh user data
            }
        }
    }

    private fun getUser() {
        runFlow {
            userRepository.getMyProfile().collect {
                sendAction(ProfileDetailAction.GetUserSuccess(it))
            }
        }
    }

    private fun updateUserProfile(newProfile: UpdateProfileData) {
        runFlow {
            userRepository.updateProfile(newProfile).collect {
                sendAction(ProfileDetailAction.UpdateProfileSuccess)
            }
        }
    }

    private fun updateAvatar(avatarUri: String) {
//        setState(currentState.copy(isLoading = true))

        // Mock implementation for now
        launch {
            try {
                // In a real implementation, we would call the API
                // userRepository.uploadAvatar(avatarUri)
                sendAction(ProfileDetailAction.UpdateAvatarSuccess)
            } catch (e: Exception) {
                sendAction(ProfileDetailAction.Error(e))
            }
        }
    }
} 
