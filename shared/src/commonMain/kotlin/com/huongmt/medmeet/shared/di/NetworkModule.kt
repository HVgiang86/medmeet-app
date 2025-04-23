package com.huongmt.medmeet.shared.di

import com.huongmt.medmeet.shared.config.NETWORK_TIMEOUT
import com.huongmt.medmeet.shared.core.datasource.network.auth.CustomBearerAuthPlugin
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        single {
            createHttpClient(get())
        }
    }

// expect fun createHttpClient(withLog: Boolean, tokenRepository: TokenRepository): HttpClient

private fun createHttpClient(tokenRepository: TokenRepository): HttpClient =
    HttpClient(CIO) {
        engine {
            maxConnectionsCount = 100
            endpoint {
                connectTimeout = NETWORK_TIMEOUT
                connectAttempts = 3
                keepAliveTime = 5000
                requestTimeout = NETWORK_TIMEOUT
            }
        }

        install(ContentNegotiation) {
            json(
                json =
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                    coerceInputValues = true
                },
                contentType = ContentType.Any
            )
        }

        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        Napier.i(tag = "KTOR", message = "Logger Ktor => $message")
                    }
                }
            level = LogLevel.ALL
        }

        install(ResponseObserver) {
            onResponse { response ->
                Napier.i(tag = "KTOR", message = "HTTP status: ${response.status.value}")
            }
        }

        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        Napier.i(tag = "KTOR", message = "Logger Ktor => $message")
                    }
                }
            level = LogLevel.ALL
            logger =
                object : Logger {
                    override fun log(message: String) {
                        if (message.startsWith("REQUEST:")) {
                            val curlCommand =
                                buildString {
                                    append("curl -X GET '") // Replace GET with the actual HTTP method
                                    append(message.substringAfter("REQUEST: ").trim())
                                    append("'")
                                }
                            Napier.i(tag = "KTOR", message = "cURL: $curlCommand")
                        }
                    }
                }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = NETWORK_TIMEOUT
            connectTimeoutMillis = NETWORK_TIMEOUT
            socketTimeoutMillis = NETWORK_TIMEOUT
        }

        install(CustomBearerAuthPlugin) {
            this.tokenRepository = tokenRepository
            excludedUrls = listOf("sign-in", "sign-up", "refresh-token")
        }
    }
