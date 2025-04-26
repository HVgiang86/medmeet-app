package com.huongmt.medmeet.shared.core.datasource.network

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicListResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.HealthRecordResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ProfileResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.UpdateHealthRecord
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class APIs(private val httpClient: HttpClient) {
    companion object {
        const val REFRESH_TOKEN_ROUTE = "auth/refresh-token"
        const val LOGIN_ROUTE = "auth/sign-in"
        const val SIGN_UP_ROUTE = "auth/sign-up"
        const val USER_API_ROUTE = "user/"
        const val CLINIC_API_ROUTE = "clinic"
        const val HEALTH_RECORD_API_ROUTE = "health-record"
    }

    suspend fun login(email: String, password: String): BaseResponse<LoginResponse> =
        httpClient.post(BASE_URL + LOGIN_ROUTE) {
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

    suspend fun getMyProfile(): BaseResponse<ProfileResponse> =
        httpClient.get(BASE_URL + USER_API_ROUTE + "me").body()

    suspend fun getClinics(
        name: String? = null,
        address: String? = null,
        status: Int? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): BaseResponse<ClinicListResponse> = httpClient.get("$BASE_URL$CLINIC_API_ROUTE") {
        // Add non-null parameters to the request
        name?.let { parameter("name", it) }
        address?.let { parameter("address", it) }
        status?.let { parameter("status", it) }
        page?.let { parameter("_page", it) }
        pageSize?.let { parameter("_pageSize", it) }
    }.body()

    suspend fun getClinicById(id: String): BaseResponse<ClinicResponse> =
        httpClient.get("$BASE_URL$CLINIC_API_ROUTE/$id").body()

    suspend fun getHealthRecord(id: String): BaseResponse<HealthRecordResponse> =
        httpClient.get("$BASE_URL$HEALTH_RECORD_API_ROUTE/$id").body()

    suspend fun updateHealthRecord(
        id: String,
        bloodType: String,
        height: Int,
        weight: Int,
        healthHistory: String
    ): BaseResponse<UpdateHealthRecord> = httpClient.put(
        "$BASE_URL$HEALTH_RECORD_API_ROUTE/$id"
    ) {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("bloodType", bloodType)
                    append("height", height.toString())
                    append("weight", weight.toString())
                    append("healthHistory", healthHistory)
                }
            )
        )
    }.body()
}
