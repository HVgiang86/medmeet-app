package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage

data class RootState(
    val isLoading: Boolean = false
) : Store.State(loading = isLoading)

sealed interface RootAction : Store.Action {
    data object Init : RootAction
    data object ShowOnBoarding : RootAction
    data object ShowLogin : RootAction
    data object ShowMain : RootAction

    data class SetFirstRun(
        val isFirstRun: Boolean
    ) : RootAction

    data class SetLoggedIn(
        val isLoggedIn: Boolean
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
    private val prefs: PrefsStorage
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
            is RootAction.SetLoggedIn -> {
                setLoggedIn(action.isLoggedIn)
            }
        }
    }

    private fun init() {
        launch {
            val isFirstRun = prefs.getBoolean(PrefsStorage.KEY_IS_ONBOARD_SHOWN, true)
            val isLoggedIn = prefs.getBoolean(PrefsStorage.KEY_IS_LOGIN)
            if (isFirstRun) {
                sendAction(RootAction.ShowOnBoarding)
            } else if (isLoggedIn) {
                sendAction(RootAction.ShowMain)
            } else {
                sendAction(RootAction.ShowLogin)
            }
        }
    }

    private fun setFirstRun(isFirstRun: Boolean) {
        launch {
            prefs.putBoolean(PrefsStorage.KEY_IS_ONBOARD_SHOWN, isFirstRun)
        }
    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        launch {
            prefs.putBoolean(PrefsStorage.KEY_IS_LOGIN, isLoggedIn)
        }
    }
}
