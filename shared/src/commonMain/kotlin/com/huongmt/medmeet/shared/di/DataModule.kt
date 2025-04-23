package com.huongmt.medmeet.shared.di

import com.huongmt.medmeet.shared.core.createSettings
import com.huongmt.medmeet.shared.core.datasource.network.MockChatApi
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorageImpl
import com.huongmt.medmeet.shared.core.repository.ChatRepository
import com.huongmt.medmeet.shared.core.repository.ChatRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import com.huongmt.medmeet.shared.core.repository.TokenRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.UserRepository
import com.huongmt.medmeet.shared.core.repository.UserRepositoryImpl
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

private val apiModule = module {
    single<MockChatApi> {
        MockChatApi(get())
    }
}

private val repositoryModule = module {
    // Define your repositories here
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<TokenRepository> { TokenRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}

val dataModule = module {
    includes(preferencesSourceModule, dispatcherModule, apiModule, repositoryModule)
}
