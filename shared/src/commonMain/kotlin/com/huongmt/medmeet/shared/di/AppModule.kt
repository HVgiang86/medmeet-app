package com.huongmt.medmeet.shared.di

import org.koin.core.module.Module

val appModules: List<Module>
    get() = listOf(networkModule, dataModule, storeModule)
