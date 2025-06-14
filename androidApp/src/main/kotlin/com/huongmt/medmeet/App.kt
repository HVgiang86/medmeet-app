package com.huongmt.medmeet

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.di.appModules
import com.huongmt.medmeet.utils.LanguageManager
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initializeLanguage()
//        launchBackgroundSync()
        Napier.base(DebugAntilog())
        Napier.d("Application Started")
    }

    override fun attachBaseContext(base: Context?) {
        // Apply language from system locale first (fallback)
        val context = base?.let { baseContext ->
            val savedLanguage = LanguageManager.getCurrentLanguageFromSystemLocale(baseContext)
            LanguageManager.setLocale(baseContext, savedLanguage)
        } ?: base
        super.attachBaseContext(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle configuration changes
    }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)

            androidContext(this@App)
            modules(appModules)
        }
    }
    
    private fun initializeLanguage() {
        // After Koin is initialized, load and apply the saved language
        applicationScope.launch {
            try {
                val prefsStorage: PrefsStorage = get()
                val savedLanguage = LanguageManager.getCurrentLanguage(prefsStorage)
                LanguageManager.setLocale(this@App, savedLanguage)
                Napier.d("Language initialized: ${savedLanguage.code} (${savedLanguage.nativeName})")
            } catch (e: Exception) {
                Napier.e("Failed to initialize language: ${e.message}")
            }
        }
    }

//    private fun launchBackgroundSync() {
//        RefreshWorker.enqueue(this)
//    }
}
