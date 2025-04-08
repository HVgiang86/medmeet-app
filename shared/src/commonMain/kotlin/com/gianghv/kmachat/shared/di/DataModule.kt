package com.gianghv.kmachat.shared.di

import com.gianghv.kmachat.shared.core.createSettings
import com.gianghv.kmachat.shared.core.datasource.prefs.PrefsStorage
import com.gianghv.kmachat.shared.core.datasource.prefs.PrefsStorageImpl
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

private val dispatcherModule = module {
    single<CoroutineDispatcher> {
        Dispatchers.IO
    }
}

private val preferencesSourceModule = module {
    single<Settings> {
        createSettings()
    }
    single<PrefsStorage> {
        PrefsStorageImpl(get())
    }
}

val dataModule = module {
    includes(preferencesSourceModule, dispatcherModule)
}
