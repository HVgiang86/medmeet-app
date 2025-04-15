package com.gianghv.kmachat.shared.di

import com.gianghv.kmachat.shared.app.chat.ChatStore
import org.koin.dsl.module

val storeModule =
    module {
        single<ChatStore> { ChatStore(get()) }
    }
