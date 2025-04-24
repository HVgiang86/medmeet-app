package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.ErrorException
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

enum class AuthScreens {
    Login, SignUp
}

data class AuthState(
    val isLoading: Boolean = false,
    val rememberEmail: String? = null,
    val currentScreen: AuthScreens = AuthScreens.Login,
    val error: Throwable? = null,
    val validateError: Throwable? = null
) : Store.State(loading = isLoading)

sealed interface AuthAction : Store.Action {
    data object Init : AuthAction
    data object DisplayLogin : AuthAction
    data object DisplaySignUp : AuthAction
    data class SignUpSuccess(val rememberEmail: String) : AuthAction
    data class RequestLogin(
        val email: String,
        val password: String
    ) : AuthAction

    data class RequestSignUp(
        val data: SignUpData
    ) : AuthAction

    data class DoLogin(
        val email: String,
        val password: String
    ) : AuthAction

    data class DoSignUp(
        val signUpData: SignUpData
    ) : AuthAction

    data object LoginSuccess : AuthAction
    data class Error(val error: Throwable) : AuthAction
    data class ValidateError(val error: Throwable) : AuthAction
    data object DismissError : AuthAction
}

sealed class AuthEffect : Store.Effect {
    data object NavigateMain : AuthEffect()
}

class AuthStore(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : Store<AuthState, AuthAction, AuthEffect>(
    initialState = AuthState(
        isLoading = false,
        rememberEmail = null,
        currentScreen = AuthScreens.Login
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            if (it is ErrorException) {
                Napier.e("Error: It is ErrorException ${it.error}")
            }

            sendAction(AuthAction.Error(it))
        }

    override fun dispatch(oldState: AuthState, action: AuthAction) {
        when (action) {
            AuthAction.DisplayLogin -> {
                setState(oldState.copy(isLoading = false, currentScreen = AuthScreens.Login))
            }

            AuthAction.DisplaySignUp -> {
                setState(oldState.copy(isLoading = false, currentScreen = AuthScreens.SignUp))
            }

            AuthAction.Init -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        rememberEmail = null,
                        currentScreen = AuthScreens.Login
                    )
                )
            }

            is AuthAction.RequestLogin -> {
                loginVerify(action.email, action.password)
            }

            AuthAction.LoginSuccess -> {
                setState(oldState.copy(isLoading = false))
                setEffect(AuthEffect.NavigateMain)
            }

            is AuthAction.RequestSignUp -> {
                signUpVerify(action.data)
            }

            is AuthAction.SignUpSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        rememberEmail = action.rememberEmail,
                        currentScreen = AuthScreens.Login
                    )
                )
            }

            is AuthAction.DoLogin -> {
                setState(oldState.copy(isLoading = true))
                login(action.email, action.password)
            }

            is AuthAction.DoSignUp -> {
                setState(oldState.copy(isLoading = true))
                signUp(action.signUpData)
            }

            is AuthAction.Error -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = action.error,
                        validateError = null
                    )
                )
            }

            is AuthAction.ValidateError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        validateError = action.error,
                        error = null
                    )
                )
            }

            AuthAction.DismissError -> {
                setState(oldState.copy(isLoading = false, error = null, validateError = null))
            }
        }
    }

    private fun loginVerify(
        email: String,
        password: String
    ) {
        runFlow {
            Napier.d("Try login with email: $email, password: $password")
            val emailError = Validator.validateEmail(email)
            val passwordError = Validator.validatePassword(password)

            if (emailError.isNullOrEmpty() && passwordError.isNullOrEmpty()) {
                sendAction(AuthAction.DoLogin(email, password))
            } else {
                if (!emailError.isNullOrEmpty()) {
                    sendAction(AuthAction.ValidateError(Exception(emailError)))
                }
                if (!passwordError.isNullOrEmpty()) {
                    sendAction(AuthAction.ValidateError(Exception(passwordError)))
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        runFlow {
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

                    sendAction(AuthAction.LoginSuccess)
                } else {
                    sendAction(AuthAction.Error(Exception("Login fail")))
                }
            }
        }
    }

    private fun signUpVerify(signUpData: SignUpData) {
        val email = signUpData.email
        val password = signUpData.password
        val name = signUpData.name
        val confirmPassword = signUpData.confirmPassword
        val province = signUpData.province
        val district = signUpData.district
        val commune = signUpData.commune
        val address = signUpData.address

        runFlow {
            val emailError = Validator.validateEmail(email)
            val passwordError = Validator.validatePassword(password)

            val nameError = if (name.isBlank()) "Name is required" else null
            val confirmPasswordError =
                if (password != confirmPassword) "Confirm password is not match" else null

            val provinceError = if (province.isBlank()) "Province is required" else null
            val districtError = if (district.isBlank()) "District is required" else null
            val communeError = if (commune.isBlank()) "Commune is required" else null
            val addressError = if (address.isBlank()) "Address is required" else null

            if (!emailError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(emailError)))
                return@runFlow
            }

            if (!passwordError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(passwordError)))
                return@runFlow
            }

            if (!nameError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(nameError)))
                return@runFlow
            }

            if (!confirmPasswordError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(confirmPasswordError)))
                return@runFlow
            }

            if (!provinceError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(provinceError)))
                return@runFlow
            }

            if (!districtError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(districtError)))
                return@runFlow
            }

            if (!communeError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception("Commune is required")))
                return@runFlow
            }

            if (!addressError.isNullOrEmpty()) {
                sendAction(AuthAction.ValidateError(Exception(addressError)))
                return@runFlow
            }

            sendAction(AuthAction.DoSignUp(signUpData))
        }
    }

    private fun signUp(signUpData: SignUpData) {
        val email = signUpData.email
        val password = signUpData.password
        val name = signUpData.name
        val dob = signUpData.dob
        val gender = signUpData.gender
        val province = signUpData.province
        val district = signUpData.district
        val commune = signUpData.commune
        val address = signUpData.address

        runFlow {
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
                commune = commune,
                address = address
            )

            userRepository.signUp(signUpRequest).collect {
                if (it) {
                    sendAction(AuthAction.SignUpSuccess(email))
                } else {
                    sendAction(AuthAction.Error(Exception("Sign up fail")))
                }
            }
        }
    }
}
