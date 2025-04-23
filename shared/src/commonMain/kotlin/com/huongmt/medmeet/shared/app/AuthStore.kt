package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.entity.SignUpData
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import com.huongmt.medmeet.shared.utils.validate.Validator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

data class AuthState(
    val isLoading: Boolean = false,
    val rememberEmail: String? = null
) : Store.State(loading = isLoading)

sealed interface AuthAction : Store.Action {
    data object Init : AuthAction
    data object DisplayLogin : AuthAction
    data object DisplaySignUp : AuthAction
    data class SignUpSuccess(val rememberEmail: String) : AuthAction
    data class Login(
        val email: String,
        val password: String
    ) : AuthAction

    data class SignUp(
        val email: String,
        val password: String
    ) : AuthAction

    data object DismissValidateError : AuthAction
    data object LoginSuccess : AuthAction
    data object Error : AuthAction
}

sealed class AuthEffect : Store.Effect {
    data object NavigateMain : AuthEffect()
    data object NavigateLogin : AuthEffect()
    data object NavigateSignUp : AuthEffect()
    data class ShowError(val error: Throwable) : AuthEffect()
    data class ShowValidateError(val error: Throwable) : AuthEffect()
}

class AuthStore(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : Store<AuthState, AuthAction, AuthEffect>(
    initialState = AuthState(
        isLoading = false,
        rememberEmail = null
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            setEffect(
                AuthEffect.ShowError(it)
            )
        }

    override fun dispatch(oldState: AuthState, action: AuthAction) {
        when (action) {
            AuthAction.DismissValidateError -> TODO()
            AuthAction.DisplayLogin -> TODO()
            AuthAction.DisplaySignUp -> TODO()
            AuthAction.Error -> TODO()
            AuthAction.Init -> TODO()
            is AuthAction.Login -> TODO()
            AuthAction.LoginSuccess -> TODO()
            is AuthAction.SignUp -> TODO()
            is AuthAction.SignUpSuccess -> TODO()
        }
    }

    private fun login(email: String, password: String) {
        launch {
//            val emailError = ValidateHelper.validateEmail(email)
            val emailError = Validator.validateNotEmpty(email)
//            val passwordError = ValidateHelper.validatePassword(password)
            val passwordError = Validator.validateNotEmpty(password)
            Napier.d("Try login with email: $email, password: $password")

            if (emailError.isNullOrEmpty() && passwordError.isNullOrEmpty()) {
                userRepository.login(email, password).collect {
                    if (!it.id.isNullOrEmpty() && !it.accessToken.isNullOrEmpty()) {
                        WholeApp.USER_ID = it.id

                        withContext(Dispatchers.IO) {
                            tokenRepository.setToken(it.accessToken)
                            tokenRepository.setRefreshToken(it.refreshToken ?: "")
                            userRepository.saveLocalUserId(it.id)
                        }

                        userRepository.getMyProfile().collect { user ->
                            Napier.d("Profile: $it")
                            WholeApp.USER = user
                        }

                        setEffect(AuthEffect.NavigateMain)
                    } else {
                        setEffect(AuthEffect.ShowError(Exception("Login fail")))
                    }
                }
            } else {
                if (!emailError.isNullOrEmpty()) {
                    setEffect(AuthEffect.ShowValidateError(Exception(emailError)))
                }
                if (!passwordError.isNullOrEmpty()) {
                    setEffect(AuthEffect.ShowValidateError(Exception(passwordError)))
                }
            }
        }
    }

    fun signUp(signUpData: SignUpData) {
        val email = signUpData.email
        val password = signUpData.password
        val name = signUpData.name
        val confirmPassword = signUpData.confirmPassword
        val dob = signUpData.dob
        val gender = signUpData.gender
        val province = signUpData.province
        val district = signUpData.district
        val address = signUpData.address

        launch {
//            val emailError = ValidateHelper.validateEmail(email)
            val emailError = Validator.validateNotEmpty(email)
//            val passwordError = ValidateHelper.validatePassword(password)
            val passwordError = Validator.validateNotEmpty(password)

            val nameError = if (name.isBlank()) "Name is required" else null
            val confirmPasswordError =
                if (password != confirmPassword) "Confirm password is not match" else null

            val provinceError = if (province.isBlank()) "Province is required" else null
            val districtError = if (district.isBlank()) "District is required" else null
            val addressError = if (address.isBlank()) "Address is required" else null

            if (!emailError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(emailError)))
                return@launch
            }

            if (!passwordError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(passwordError)))
                return@launch
            }

            if (!nameError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(nameError)))
                return@launch
            }

            if (!confirmPasswordError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(confirmPasswordError)))
                return@launch
            }

            if (!provinceError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(provinceError)))
                return@launch
            }

            if (!districtError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(districtError)))
                return@launch
            }

            if (!addressError.isNullOrEmpty()) {
                setEffect(AuthEffect.ShowValidateError(Exception(addressError)))
                return@launch
            }

            val dobText = dob.toString()
            val genderText = gender.value
            val signUpRequest = SignUpRequest(
                email = email,
                password = password,
                name = name,
                dob = dobText,
                gender = genderText,
                province = province,
                district = district,
                address = address
            )

            userRepository.signUp(signUpRequest).collect {
                if (it) {
                    sendAction(AuthAction.SignUpSuccess(email))
                } else {
                    setEffect(AuthEffect.ShowError(Exception("Sign up fail")))
                }
            }
        }
    }

    fun clearToken() {
        launch {
            tokenRepository.clearTokens()
            Napier.d("Token cleared")
        }
    }
}
