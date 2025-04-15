package com.gianghv.kmachat.shared.core.datasource.network

import com.gianghv.kmachat.shared.core.entity.Feed
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal class FeedLoader(
    private val httpClient: HttpClient,
    private val parser: FeedParser
) {
    suspend fun getFeed(
        url: String,
        isDefault: Boolean
    ): Feed {
        val xml = httpClient.get(url).bodyAsText()
        return parser.parse(url, xml, isDefault)
    }
}
