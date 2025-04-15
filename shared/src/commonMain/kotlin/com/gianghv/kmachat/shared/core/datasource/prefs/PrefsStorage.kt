package com.gianghv.kmachat.shared.core.datasource.prefs

import com.russhwolf.settings.Settings

interface PrefsStorage {
    companion object Keys {
        const val KEY_IS_ONBOARD_SHOWN = "KEY_IS_ONBOARD_SHOWN"
        const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val TOKEN = "TOKEN"
        const val REFRESH_TOKEN = "REFRESH_TOKEN"
        const val KEY_RECOMMENDATION_URL = "RECOMMENDATION_URL"
        const val KEY_CHAT_URL = "CHAT_URL"
        const val KEY_ORDER_INFO = "KEY_ORDER_INFO"
    }

    suspend fun getString(
        key: String,
        defaultValue: String? = null
    ): String?

    suspend fun getInt(
        key: String,
        defaultValue: Int? = null
    ): Int?

    suspend fun getLong(
        key: String,
        defaultValue: Long? = null
    ): Long?

    suspend fun getBoolean(
        key: String,
        defaultValue: Boolean = false
    ): Boolean

    suspend fun putString(
        key: String,
        value: String
    )

    suspend fun putInt(
        key: String,
        value: Int
    )

    suspend fun putLong(
        key: String,
        value: Long
    )

    suspend fun putBoolean(
        key: String,
        value: Boolean
    )

    suspend fun clear()
}

class PrefsStorageImpl(
    private val settings: Settings
) : PrefsStorage {
    override suspend fun getString(
        key: String,
        defaultValue: String?
    ): String? = settings.getStringOrNull(key).takeIf { it != null } ?: defaultValue

    override suspend fun getInt(
        key: String,
        defaultValue: Int?
    ): Int? = settings.getIntOrNull(key).takeIf { it != null } ?: defaultValue

    override suspend fun getLong(
        key: String,
        defaultValue: Long?
    ): Long? = settings.getLongOrNull(key).takeIf { it != null } ?: defaultValue

    override suspend fun getBoolean(
        key: String,
        defaultValue: Boolean
    ): Boolean = settings.getBoolean(key, defaultValue)

    override suspend fun putString(
        key: String,
        value: String
    ) {
        settings.putString(key, value)
    }

    override suspend fun putInt(
        key: String,
        value: Int
    ) {
        settings.putInt(key, value)
    }

    override suspend fun putLong(
        key: String,
        value: Long
    ) {
        settings.putLong(key, value)
    }

    override suspend fun putBoolean(
        key: String,
        value: Boolean
    ) {
        settings.putBoolean(key, value)
    }

    override suspend fun clear() {
        settings.clear()
    }
}
