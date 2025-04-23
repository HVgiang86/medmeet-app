package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage

interface TokenRepository {
    suspend fun getToken(): String?
    suspend fun setToken(token: String)
    suspend fun clearToken()
    suspend fun getRefreshToken(): String?
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun clearRefreshToken()
    suspend fun setTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}

class TokenRepositoryImpl(private val prefs: PrefsStorage) : TokenRepository {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    override suspend fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    override suspend fun setToken(token: String) {
        prefs.putString(KEY_ACCESS_TOKEN, token)
    }

    override suspend fun clearToken() {
        prefs.putString(KEY_ACCESS_TOKEN, "")
        prefs.putString(KEY_REFRESH_TOKEN, "")
    }

    override suspend fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        prefs.putString(KEY_REFRESH_TOKEN, refreshToken)
    }

    override suspend fun clearRefreshToken() {
        prefs.putString(KEY_REFRESH_TOKEN, "")
    }

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        prefs.putString(KEY_ACCESS_TOKEN, accessToken)
        prefs.putString(KEY_REFRESH_TOKEN, refreshToken)
    }

    override suspend fun clearTokens() {
        prefs.putString(KEY_ACCESS_TOKEN, "")
        prefs.putString(KEY_REFRESH_TOKEN, "")
    }
}
