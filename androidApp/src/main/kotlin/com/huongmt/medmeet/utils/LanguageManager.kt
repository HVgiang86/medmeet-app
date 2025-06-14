package com.huongmt.medmeet.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.entity.Language
import java.util.Locale

object LanguageManager {
    private const val TAG = "LanguageManager"
    
    fun setLocale(context: Context, language: Language): Context {
        Log.d(TAG, "Setting locale to: ${language.code} (${language.nativeName})")
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale)
        } else {
            updateResourcesLegacy(context, locale)
        }
    }
    
    suspend fun setAndPersistLanguage(context: Context, language: Language, prefsStorage: PrefsStorage): Context {
        Log.d(TAG, "Setting and persisting language: ${language.code} (${language.nativeName})")
        
        // Save to preferences first
        prefsStorage.putString(PrefsStorage.Keys.KEY_LANGUAGE, language.code)
        
        // Then apply to current context
        return setLocale(context, language)
    }
    
    fun wrapContext(context: Context, language: Language): ContextWrapper {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        val localizedContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
        
        return ContextWrapper(localizedContext)
    }
    
    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }
    
    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
    
    suspend fun getCurrentLanguage(prefsStorage: PrefsStorage): Language {
        // Get saved language from preferences using PrefsStorage
        try {
            val savedLanguageCode = prefsStorage.getString(PrefsStorage.Keys.KEY_LANGUAGE)
            Log.d(TAG, "Saved language code from preferences: $savedLanguageCode")
            if (savedLanguageCode != null) {
                val language = Language.fromCode(savedLanguageCode)
                Log.d(TAG, "Returning saved language: ${language.code} (${language.nativeName})")
                return language
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading preferences: ${e.message}")
        }
        
        // Fall back to default language
        val language = Language.getDefault()
        Log.d(TAG, "Fallback to default language: ${language.code} (${language.nativeName})")
        return language
    }
    
    // Fallback method for when PrefsStorage is not available (like in App.attachBaseContext)
    fun getCurrentLanguageFromSystemLocale(context: Context): Language {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        val language = Language.fromCode(currentLocale.language)
        Log.d(TAG, "Language from system locale: ${language.code} (${language.nativeName})")
        return language
    }
    
    fun recreateActivity(activity: Activity) {
        Log.d(TAG, "Recreating activity: ${activity.javaClass.simpleName}")
        activity.recreate()
    }
} 