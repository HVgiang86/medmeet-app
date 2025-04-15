package com.gianghv.kmachat

import android.app.Application
import com.gianghv.kmachat.shared.app.FeedStore
import com.gianghv.kmachat.shared.core.RssReader
import com.gianghv.kmachat.shared.core.create
import com.gianghv.kmachat.shared.di.appModules
import com.gianghv.kmachat.sync.RefreshWorker
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        launchBackgroundSync()
        Napier.base(DebugAntilog())
        Napier.d("Application Started")
    }

    private val readerModules =
        module {
            single { RssReader.create(get(), BuildConfig.DEBUG) }
            single { FeedStore(get()) }
        }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)

            androidContext(this@App)
            modules(readerModules)
            modules(appModules)
        }
    }

    private fun launchBackgroundSync() {
        RefreshWorker.enqueue(this)
    }
}
