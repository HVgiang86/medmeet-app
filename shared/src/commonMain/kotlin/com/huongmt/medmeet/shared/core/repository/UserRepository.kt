package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage.Keys.KEY_BACKEND_URL
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage.Keys.KEY_CHAT_URL
import com.huongmt.medmeet.shared.core.datasource.prefs.PrefsStorage.Keys.KEY_USER_ID
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.core.maper.toUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Flow<LoginResponse>
    suspend fun signUp(user: SignUpRequest): Flow<Boolean>
    suspend fun getMyProfile(): Flow<User>
    suspend fun getProfileById(id: String): Flow<User>
    suspend fun saveLocalUserId(userId: String)
    suspend fun getLocalUserId(): Flow<String>
    suspend fun clearLocalUserId()
    suspend fun getChatBaseUrl(): Flow<String>
    suspend fun setChatBaseUrl(url: String)
    suspend fun getBackendUrl(): Flow<String>
    suspend fun setBackendUrl(url: String)
}

class UserRepositoryImpl(private val api: APIs, private val prefs: PrefsStorage) : UserRepository,
    BaseRepository() {
    override suspend fun login(email: String, password: String) = flowContext {
        api.login(email, password)
    }

    override suspend fun signUp(user: SignUpRequest): Flow<Boolean> = returnIfSuccess {
        api.signUp(user)
    }

    override suspend fun getMyProfile(): Flow<User> = flowContext(mapper = {
        it.toUser()
    }, block = {
        api.getMyProfile()
    })

    override suspend fun getProfileById(id: String) = flowContext(mapper = {
        it.toUser()
    }, block = {
        api.getProfileById(id)
    })

    override suspend fun saveLocalUserId(userId: String) {
        prefs.putString(KEY_USER_ID, userId)
    }

    override suspend fun getLocalUserId() = launchResult {
        prefs.getString(KEY_USER_ID)
    }

    override suspend fun clearLocalUserId() {
        prefs.putString(KEY_USER_ID, "")
    }

    override suspend fun getChatBaseUrl(): Flow<String> = launchResult {
        prefs.getString(KEY_CHAT_URL, "")
    }

    override suspend fun setChatBaseUrl(url: String) {
        prefs.putString(KEY_CHAT_URL, url)
    }

    override suspend fun getBackendUrl(): Flow<String> = launchResult {
        prefs.getString(KEY_BACKEND_URL, "")
    }

    override suspend fun setBackendUrl(url: String) {
        prefs.putString(KEY_BACKEND_URL, url)
    }
}
