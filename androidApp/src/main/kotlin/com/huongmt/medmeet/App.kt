package com.huongmt.medmeet

import android.app.Application
import com.huongmt.medmeet.shared.di.appModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
//        launchBackgroundSync()
        Napier.base(DebugAntilog())
        Napier.d("Application Started")
    }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)

            androidContext(this@App)
            modules(appModules)
        }
    }

//    private fun launchBackgroundSync() {
//        RefreshWorker.enqueue(this)
//    }
}
