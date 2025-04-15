package com.gianghv.kmachat.shared.core

import com.gianghv.kmachat.shared.core.datasource.network.FeedLoader
import com.gianghv.kmachat.shared.core.datasource.storage.FeedStorage
import com.russhwolf.settings.NSUserDefaultsSettings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

fun RssReader.Companion.create(withLog: Boolean) =
    RssReader(
        FeedLoader(
            IosHttpClient(withLog),
            IosFeedParser()
        ),
        FeedStorage(
            NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults()),
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
        )
    ).also {
        if (withLog) Napier.base(DebugAntilog())
    }
