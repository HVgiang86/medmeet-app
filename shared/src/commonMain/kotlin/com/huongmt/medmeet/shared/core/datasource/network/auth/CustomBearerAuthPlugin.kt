package com.huongmt.medmeet.shared.core.datasource.network.auth

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.repository.TokenRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CustomBearerAuthPlugin private constructor(
    private val config: Config
) {
    class Config {
        var excludedUrls: List<String> = listOf("sign-in", "sign-up", "refresh-token")
        var tokenRepository: TokenRepository? = null
    }

    companion object Plugin : HttpClientPlugin<Config, CustomBearerAuthPlugin> {
        override val key = AttributeKey<CustomBearerAuthPlugin>("BearerAuth")
        private val mutex = Mutex()

        override fun prepare(block: Config.() -> Unit): CustomBearerAuthPlugin {
            val config = Config().apply(block)
            require(config.tokenRepository != null) { "TokenRepository must be provided" }
            return CustomBearerAuthPlugin(config)
        }

        override fun install(plugin: CustomBearerAuthPlugin, scope: HttpClient) {
            val tokenRepository = plugin.config.tokenRepository!!
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                val url = context.url.toString()

                // Skip authentication for excluded URLs
                val shouldSkip = plugin.config.excludedUrls.any { url.contains(it, ignoreCase = true) }
                if (shouldSkip) return@intercept

                // Load token for every request that's not excluded
                Napier.d("[DEBUG] LoadToken was called for request: $url")
                val accessToken = tokenRepository.getToken()
                Napier.d("[DEBUG] Access token: $accessToken")
                val refreshToken = tokenRepository.getRefreshToken()

                if (!accessToken.isNullOrBlank()) {
                    context.headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
            }

            scope.plugin(HttpSend).intercept { builder ->
                val response = execute(builder)

                if (response.response.status == HttpStatusCode.Unauthorized) {
                    Napier.d("[DEBUG] Unauthorized response received, refreshing token...")

                    if (builder.url.toString().contains("refresh-token", ignoreCase = true)) {
                        Napier.d("[DEBUG] Refresh token request failed, skipping...")
                        return@intercept response
                    }

                    // Refresh token logic
                    val refreshToken = tokenRepository.getRefreshToken()
                    if (!refreshToken.isNullOrBlank()) {
                        try {
                            Napier.d("[DEBUG] Refreshing token with refresh token: $refreshToken")

                            val resp = scope.post("${BASE_URL}${APIs.REFRESH_TOKEN_ROUTE}") {
                                contentType(ContentType.Application.Json)
                                setBody(mapOf("refresh_token" to refreshToken))
                            }

                            val refreshResponse: BaseResponse<LoginResponse> = resp.body()

                            val data = refreshResponse.getSuccessfulData()
                            if (resp.status.isSuccess()) {
                                val newAccessToken = data.accessToken
                                val newRefreshToken = data.refreshToken

                                if (!newAccessToken.isNullOrBlank()) {
                                    mutex.withLock {
                                        tokenRepository.setToken(newAccessToken)
                                        tokenRepository.setRefreshToken(newRefreshToken ?: "")
                                    }
                                    Napier.d("[DEBUG] Token refreshed successfully")

                                    // Retry the original request with the new token
                                    val newRequest = builder.apply {
                                        headers.remove(HttpHeaders.Authorization)
                                        headers.append(HttpHeaders.Authorization, "Bearer $newAccessToken")
                                    }

                                    return@intercept execute(newRequest)
                                }
                            } else {
                                Napier.e("[DEBUG] Failed to refresh token: ${refreshResponse.message}")
                                tokenRepository.clearTokens()
                            }
                        } catch (e: Exception) {
                            Napier.e("[DEBUG] Error refreshing token: ${e.message}")
                            tokenRepository.clearTokens()
                        }
                    }
                }

                response
            }
        }
    }

    // Factory method to create with custom tokenRepository
    class Factory(private val tokenRepository: TokenRepository) {
        fun create(config: Config = Config()): CustomBearerAuthPlugin {
            config.tokenRepository = tokenRepository
            return CustomBearerAuthPlugin(config)
        }
    }
}
