package com.huongmt.medmeet.shared.di

import com.huongmt.medmeet.shared.core.createSettings
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorageImpl
import com.huongmt.medmeet.shared.core.repository.BookingRepository
import com.huongmt.medmeet.shared.core.repository.ChatRepository
import com.huongmt.medmeet.shared.core.repository.ChatRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.ClinicRepository
import com.huongmt.medmeet.shared.core.repository.ClinicRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.HealthRecordRepository
import com.huongmt.medmeet.shared.core.repository.HealthRecordRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.MedicalRepository
import com.huongmt.medmeet.shared.core.repository.MedicalRepositoryImpl
import com.huongmt.medmeet.shared.core.repository.MockBookingRepository
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
    single<APIs> {
        APIs(get())
    }
}

private val repositoryModule = module {
    // Define your repositories here
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<TokenRepository> { TokenRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ClinicRepository> { ClinicRepositoryImpl(get()) }
    single<MedicalRepository> { MedicalRepositoryImpl(get()) }
    single<BookingRepository> { MockBookingRepository(get()) }
    single<HealthRecordRepository> { HealthRecordRepositoryImpl(get()) }
}

val dataModule = module {
    includes(preferencesSourceModule, dispatcherModule, apiModule, repositoryModule)
}
