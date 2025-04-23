package com.huongmt.medmeet.shared.core.datasource.network

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class APIs(private val httpClient: HttpClient) {
    companion object {
        const val REFRESH_TOKEN_ROUTE = "auth/refresh-token"
        const val LOGIN_ROUTE = "auth/sign-in"
        const val SIGN_UP_ROUTE = "auth/sign-up"
        const val USER_API_ROUTE = "user/"
    }

    suspend fun login(email: String, password: String): BaseResponse<LoginResponse> = httpClient.post(BASE_URL + LOGIN_ROUTE) {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("email", email)
                    append("password", password)
                }
            )
        )
    }.body()

    suspend fun signUp(user: SignUpRequest): BaseResponse<ProfileResponse> =
        httpClient.post(BASE_URL + SIGN_UP_ROUTE) {
            setBody(user)
        }.body()

    suspend fun getMyProfile(): BaseResponse<ProfileResponse> = httpClient.get(BASE_URL + USER_API_ROUTE + "me").body()
}
