package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.Language
import com.huongmt.medmeet.shared.core.repository.LanguageRepository
import kotlinx.coroutines.launch

// State
data class LanguageState(
    val currentLanguage: Language = Language.getDefault(),
    val availableLanguages: List<Language> = Language.values().toList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
) : Store.State(isLoading)

// Actions
sealed interface LanguageAction : Store.Action {
    data class ChangeLanguage(val language: Language) : LanguageAction
    data class ChangeLanguageSuccess(val language: Language) : LanguageAction
    data object GetCurrentLanguage : LanguageAction
    data object DismissError : LanguageAction
    data class Error(val error: Throwable) : LanguageAction
    data class LanguageLoadSuccess(val language: Language) : LanguageAction
}

// Effects
sealed interface LanguageEffect : Store.Effect {
    data class LanguageChanged(val language: Language) : LanguageEffect
}

// Store
class LanguageStore(
    private val languageRepository: LanguageRepository
) : Store<LanguageState, LanguageAction, LanguageEffect>(
    initialState = LanguageState()
) {

    override val onException: (Throwable) -> Unit
        get() = {
            println("LanguageStore - Exception: ${it.message}")
            sendAction(LanguageAction.Error(it))
        }

    init {
        observeLanguageChanges()
        sendAction(LanguageAction.GetCurrentLanguage)
    }

    override fun dispatch(oldState: LanguageState, action: LanguageAction) {
        println("LanguageStore - Dispatching action: $action")
        when (action) {
            is LanguageAction.Error -> {
                println("LanguageStore - Error: ${action.error.message}")
                setState(oldState.copy(isLoading = false, error = action.error))
            }

            is LanguageAction.ChangeLanguage -> {
                println("LanguageStore - Changing language to: ${action.language.code} (${action.language.nativeName})")
                setState(oldState.copy(isLoading = true, error = null))
                changeLanguage(action.language)
            }

            LanguageAction.GetCurrentLanguage -> {
                println("LanguageStore - Getting current language")
                setState(oldState.copy(isLoading = true, error = null))
                getCurrentLanguage()
            }

            LanguageAction.DismissError -> {
                setState(oldState.copy(error = null))
            }

            is LanguageAction.LanguageLoadSuccess -> {
                println("LanguageStore - Language loaded successfully: ${action.language.code} (${action.language.nativeName})")
                setState(oldState.copy(currentLanguage = action.language, isLoading = false, error = null))
            }

            is LanguageAction.ChangeLanguageSuccess -> {
                println("LanguageStore - Language changed successfully: ${action.language.code} (${action.language.nativeName})")
                setState(oldState.copy(currentLanguage = action.language, isLoading = false, error = null))
                setEffect(LanguageEffect.LanguageChanged(action.language))
            }
        }
    }

    private fun observeLanguageChanges() {
        launch {
            languageRepository.observeLanguage().collect { language ->
                println("LanguageStore - Language observed from repository: ${language.code} (${language.nativeName})")
                sendAction(LanguageAction.LanguageLoadSuccess(language))
            }
        }
    }

    private fun changeLanguage(language: Language) {
        runFlow {
            println("LanguageStore - Saving language to repository: ${language.code}")
            languageRepository.setLanguage(language)
            sendAction(LanguageAction.ChangeLanguageSuccess(language))
        }
    }

    private fun getCurrentLanguage() {
        runFlow {
            val currentLanguage = languageRepository.getCurrentLanguage()
            println("LanguageStore - Current language from repository: ${currentLanguage.code} (${currentLanguage.nativeName})")
            sendAction(LanguageAction.LanguageLoadSuccess(currentLanguage))
        }
    }
} 