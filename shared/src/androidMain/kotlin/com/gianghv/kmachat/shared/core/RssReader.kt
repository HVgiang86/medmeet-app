package com.gianghv.kmachat.shared.core

import android.content.Context
import com.gianghv.kmachat.shared.core.datasource.network.FeedLoader
import com.gianghv.kmachat.shared.core.datasource.storage.FeedStorage
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

fun RssReader.Companion.create(ctx: Context, withLog: Boolean) = RssReader(
    FeedLoader(
        AndroidHttpClient(withLog),
        AndroidFeedParser()
    ),
    FeedStorage(
        SharedPreferencesSettings(ctx.getSharedPreferences("rss_reader_pref", Context.MODE_PRIVATE)),
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    )
).also {
    if (withLog) Napier.base(DebugAntilog())
}
