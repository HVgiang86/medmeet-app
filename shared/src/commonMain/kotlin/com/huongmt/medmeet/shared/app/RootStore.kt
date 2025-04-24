package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import com.huongmt.medmeet.shared.core.repository.UserRepository
import io.github.aakira.napier.Napier

data class RootState(
    val isLoading: Boolean = false
) : Store.State(loading = isLoading)

sealed interface RootAction : Store.Action {
    data object Init : RootAction
    data object ShowOnBoarding : RootAction
    data object ShowLogin : RootAction
    data object ShowMain : RootAction
    data object GetProfileSuccess : RootAction
    data object GetProfileFail : RootAction

    data class SetFirstRun(
        val isFirstRun: Boolean
    ) : RootAction

    data class Error(
        val error: Throwable
    ) : RootAction
}

sealed interface RootEffect : Store.Effect {
    data class ShowError(
        val error: Throwable
    ) : RootEffect

    data object ShowOnBoarding : RootEffect

    data object ShowLogin : RootEffect

    data object ShowMain : RootEffect
}

class RootStore(
    private val prefs: PrefsStorage,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) : Store<RootState, RootAction, RootEffect>(
    RootState(
        isLoading = true
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(RootAction.Error(it))
        }

    override fun dispatch(
        oldState: RootState,
        action: RootAction
    ) {
        when (action) {
            is RootAction.Error -> {
                setEffect(RootEffect.ShowError(action.error))
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }

            RootAction.Init -> {
                setState(
                    oldState.copy(
                        isLoading = true
                    )
                )
                init()
            }

            RootAction.ShowLogin -> {
                setEffect(RootEffect.ShowLogin)
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }

            RootAction.ShowMain -> {
                setEffect(RootEffect.ShowMain)
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }

            RootAction.ShowOnBoarding -> {
                setEffect(RootEffect.ShowOnBoarding)
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }

            is RootAction.SetFirstRun -> {
                setFirstRun(action.isFirstRun)
            }

            RootAction.GetProfileFail -> {
                setEffect(RootEffect.ShowLogin)
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }

            RootAction.GetProfileSuccess -> {
                setEffect(RootEffect.ShowMain)
                setState(
                    oldState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }

    private fun init() {
        runFlow {
            val isFirstRun = prefs.getBoolean(PrefsStorage.KEY_IS_ONBOARD_SHOWN, true)
            if (isFirstRun) {
                sendAction(RootAction.ShowOnBoarding)
                setFirstRun(false)
                return@runFlow
            }

            getMe()
        }
    }

    private fun setFirstRun(isFirstRun: Boolean) {
        runFlow {
            prefs.putBoolean(PrefsStorage.KEY_IS_ONBOARD_SHOWN, isFirstRun)
        }
    }

    private fun getMe() {
        Napier.d("Get me")
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(RootAction.GetProfileFail)
            }
        ) {
            userRepository.getMyProfile().collect {
                sendAction(RootAction.GetProfileSuccess)
                WholeApp.USER = it
                WholeApp.USER_ID = it.id
            }
        }
    }
}
