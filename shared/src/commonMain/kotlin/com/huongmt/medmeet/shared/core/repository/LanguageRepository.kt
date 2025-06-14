package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.entity.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface LanguageRepository {
    suspend fun getCurrentLanguage(): Language
    suspend fun setLanguage(language: Language)
    fun observeLanguage(): Flow<Language>
}

class LanguageRepositoryImpl(
    private val prefsStorage: PrefsStorage
) : LanguageRepository {
    
    private val _currentLanguage = MutableStateFlow(Language.getDefault())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    init {
        // Load saved language on initialization
        scope.launch {
            val savedLanguageCode = prefsStorage.getString(PrefsStorage.Keys.KEY_LANGUAGE)
            val language = savedLanguageCode?.let { Language.fromCode(it) } ?: Language.getDefault()
            println("DEBUG -> LanguageRepository - Init: Loaded language $savedLanguageCode -> ${language.code} (${language.nativeName})")
            _currentLanguage.value = language
        }
    }
    
    override suspend fun getCurrentLanguage(): Language {
        val savedLanguageCode = prefsStorage.getString(PrefsStorage.Keys.KEY_LANGUAGE)
        val language = savedLanguageCode?.let { Language.fromCode(it) } ?: Language.getDefault()
        println("DEBUG -> LanguageRepository - getCurrentLanguage: $savedLanguageCode -> ${language.code} (${language.nativeName})")
        return language
    }
    
    override suspend fun setLanguage(language: Language) {
        println("DEBUG -> LanguageRepository - setLanguage: ${language.code} (${language.nativeName})")
        prefsStorage.putString(PrefsStorage.Keys.KEY_LANGUAGE, language.code)
        _currentLanguage.value = language
        println("DEBUG -> LanguageRepository - Language saved to preferences and flow updated")
    }
    
    override fun observeLanguage(): Flow<Language> {
        return _currentLanguage.asStateFlow()
    }
} 