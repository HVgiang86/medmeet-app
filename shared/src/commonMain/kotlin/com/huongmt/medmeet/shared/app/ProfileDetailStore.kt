package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.UpdateProfileData
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.repository.UserRepository
import com.huongmt.medmeet.shared.utils.validate.Validator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

data class ProfileDetailState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val validateError: Throwable? = null,
    val originalUser: User? = null,
    val isEditMode: Boolean = false,
    val pendingUpdateData: UpdateProfileData? = null
) : Store.State(isLoading)

sealed interface ProfileDetailAction : Store.Action {
    data object GetUser : ProfileDetailAction
    data class GetUserSuccess(val user: User) : ProfileDetailAction
    data class UpdateAvatar(val userId: String, val fileData: ByteArray, val fileName: String, val mimeType: String) : ProfileDetailAction
    data object ToggleEditMode : ProfileDetailAction
    data class UpdatePendingData(val updateData: UpdateProfileData) : ProfileDetailAction
    data class ValidateAndSave(val updateData: UpdateProfileData) : ProfileDetailAction
    data class SaveProfile(val updateData: UpdateProfileData) : ProfileDetailAction
    data object UpdateProfileSuccess : ProfileDetailAction
    data object UpdateAvatarSuccess : ProfileDetailAction
    data class Error(val error: Throwable) : ProfileDetailAction
    data class ValidateError(val error: Throwable) : ProfileDetailAction
    data object NavigateBack : ProfileDetailAction
    data object DismissError : ProfileDetailAction
    data object CancelEdit : ProfileDetailAction
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
                setState(oldState.copy(isLoading = false, error = action.error, validateError = null))
            }

            is ProfileDetailAction.ValidateError -> {
                setState(oldState.copy(isLoading = false, validateError = action.error, error = null))
            }

            ProfileDetailAction.GetUser -> {
                setState(oldState.copy(isLoading = true, error = null, validateError = null))
                getUser()
            }

            is ProfileDetailAction.GetUserSuccess -> {
                Napier.d { "Get user success: ${action.user}" }
                setState(
                    oldState.copy(
                        originalUser = action.user,
                        pendingUpdateData = action.user.toDateUpdate(),
                        isLoading = false,
                        error = null,
                        validateError = null
                    )
                )
            }

            ProfileDetailAction.DismissError -> {
                setState(oldState.copy(error = null, validateError = null))
            }

            ProfileDetailAction.NavigateBack -> {
                setEffect(ProfileDetailEffect.NavigateBack)
            }

            ProfileDetailAction.ToggleEditMode -> {
                if (oldState.isEditMode) {
                    // Exiting edit mode - reset to original data
                    setState(
                        oldState.copy(
                            isEditMode = false,
                            pendingUpdateData = oldState.originalUser?.toDateUpdate(),
                            error = null,
                            validateError = null
                        )
                    )
                } else {
                    // Entering edit mode - set pending data to current user data
                    setState(
                        oldState.copy(
                            isEditMode = true,
                            pendingUpdateData = oldState.originalUser?.toDateUpdate(),
                            error = null,
                            validateError = null
                        )
                    )
                }
            }

            ProfileDetailAction.CancelEdit -> {
                setState(
                    oldState.copy(
                        isEditMode = false,
                        pendingUpdateData = oldState.originalUser?.toDateUpdate(),
                        error = null,
                        validateError = null
                    )
                )
            }

            is ProfileDetailAction.UpdatePendingData -> {
                setState(oldState.copy(pendingUpdateData = action.updateData))
            }

            is ProfileDetailAction.ValidateAndSave -> {
                setState(oldState.copy(error = null, validateError = null))
                validateProfile(action.updateData)
            }

            is ProfileDetailAction.SaveProfile -> {
                setState(oldState.copy(isLoading = true, error = null, validateError = null))
                updateUserProfile(action.updateData)
            }

            is ProfileDetailAction.UpdateAvatar -> {
                setState(oldState.copy(
                    isLoading = true,
                    error = null,
                    validateError = null
                ))
                updateAvatar(action.userId, action.fileData, action.fileName, action.mimeType)
            }

            ProfileDetailAction.UpdateProfileSuccess -> {
                setEffect(ProfileDetailEffect.ShowMessage("Profile updated successfully"))
                setState(oldState.copy(isLoading = false, isEditMode = false, error = null, validateError = null))
                // Refresh user data to show updated information
                getUser()
            }

            ProfileDetailAction.UpdateAvatarSuccess -> {
                setEffect(ProfileDetailEffect.ShowMessage("Avatar updated successfully"))
                setState(oldState.copy(isLoading = false))
                // Refresh user data to show updated avatar
                getUser()
            }
        }
    }

    private fun validateProfile(updateData: UpdateProfileData) {
        runFlow {
            val nameError = if (updateData.name.isBlank()) "Name is required" else null
            val phoneError = if (updateData.phoneNumber.isBlank()) "Phone number is required" else null
            val provinceError = if (updateData.province.isBlank()) "Province is required" else null
            val districtError = if (updateData.district.isBlank()) "District is required" else null
            val communeError = if (updateData.commune.isBlank()) "Commune is required" else null
            val addressError = if (updateData.address.isBlank()) "Address is required" else null

            // Check for first error and show it
            when {
                !nameError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(nameError)))
                }
                !phoneError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(phoneError)))
                }
                !provinceError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(provinceError)))
                }
                !districtError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(districtError)))
                }
                !communeError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(communeError)))
                }
                !addressError.isNullOrEmpty() -> {
                    sendAction(ProfileDetailAction.ValidateError(Exception(addressError)))
                }
                else -> {
                    // All validation passed, proceed with save
                    sendAction(ProfileDetailAction.SaveProfile(updateData))
                }
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

    private fun updateAvatar(userId: String, fileData: ByteArray, fileName: String, mimeType: String) {
        runFlow {
            userRepository.updateAvatar(userId, fileData, fileName, mimeType).collect {
                sendAction(ProfileDetailAction.UpdateAvatarSuccess)
            }
        }
    }
} 
