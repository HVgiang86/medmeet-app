package com.huongmt.medmeet.shared.di

import com.huongmt.medmeet.shared.core.repository.TokenRepository
import io.ktor.client.HttpClient

actual fun createHttpClient(
    withLog: Boolean,
    tokenRepository: TokenRepository
): HttpClient {
    TODO("Not yet implemented")
}
