package com.huongmt.medmeet.shared.di

import com.huongmt.medmeet.shared.app.AuthStore
import com.huongmt.medmeet.shared.app.BookingDetailStore
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.app.ChatStore
import com.huongmt.medmeet.shared.app.ClinicDetailStore
import com.huongmt.medmeet.shared.app.HealthRecordStore
import com.huongmt.medmeet.shared.app.HomeStore
import com.huongmt.medmeet.shared.app.LanguageStore
import com.huongmt.medmeet.shared.app.NotificationStore
import com.huongmt.medmeet.shared.app.ProfileDetailStore
import com.huongmt.medmeet.shared.app.ProfileStore
import com.huongmt.medmeet.shared.app.RootStore
import com.huongmt.medmeet.shared.app.ScheduleStore
import org.koin.dsl.module

val storeModule = module {
    single<ChatStore> { ChatStore(get()) }
    single<RootStore> { RootStore(get(), get(), get()) }
    single<AuthStore> { AuthStore(get(), get()) }
    single<HomeStore> { HomeStore(get(), get(), get()) }
    single<ProfileStore> { ProfileStore(get(), get()) }
    single<ProfileDetailStore> { ProfileDetailStore(get()) }
    single<ClinicDetailStore> { ClinicDetailStore(get()) }
    single<ScheduleStore> { ScheduleStore(get()) }
    single<BookingStore> { BookingStore(get(), get(), get()) }
    single<BookingDetailStore> { BookingDetailStore(get(), get()) }
    single<HealthRecordStore> { HealthRecordStore(get()) }
    single { NotificationStore(get(), get()) }
    single<LanguageStore> { LanguageStore(get()) }
}
