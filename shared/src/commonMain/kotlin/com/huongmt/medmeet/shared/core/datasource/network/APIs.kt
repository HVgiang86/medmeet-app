package com.huongmt.medmeet.shared.core.datasource.network

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.request.CreateConversationRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.SendMessageRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicListResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ConversationResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.MessageResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class APIs(private val httpClient: HttpClient) {
    companion object {
        const val REFRESH_TOKEN_ROUTE = "auth/refresh-token"
        const val LOGIN_ROUTE = "auth/sign-in"
        const val SIGN_UP_ROUTE = "auth/sign-up"
        const val USER_API_ROUTE = "user/"
        const val CLINIC_API_ROUTE = "clinic"
        const val CONVERSATION_ROUTE = "api/conversations"
        const val MESSAGE_ROUTE = "api/messages"
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

    suspend fun getConversations(
        uid: String,
        skip: Int = 0,
        limit: Int = 30
    ): BaseResponse<List<ConversationResponse>> =
        httpClient.get("${WholeApp.CHAT_BASE_URL}/$CONVERSATION_ROUTE") {
            parameter("user_id", uid)
            parameter("skip", skip)
            parameter("limit", limit)
        }.body()

    suspend fun getMessagesOfConversation(
        conversationId: String,
        skip: Int = 0,
        limit: Int = 30
    ): BaseResponse<List<MessageResponse>> =
        httpClient.get("${WholeApp.CHAT_BASE_URL}/$MESSAGE_ROUTE/$conversationId") {
            parameter("skip", skip)
            parameter("limit", limit)
        }.body()

    suspend fun createConversation(uid: String, title: String): BaseResponse<ConversationResponse> =
        httpClient.post("${WholeApp.CHAT_BASE_URL}/$CONVERSATION_ROUTE") {
            setBody(CreateConversationRequest(uid = uid, title = title))
        }.body()

    suspend fun queryAiMessage(
        conversationId: String,
        message: String
    ): BaseResponse<MessageResponse> =
        httpClient.post("${WholeApp.CHAT_BASE_URL}/api/$conversationId/messages") {
            setBody(
                SendMessageRequest(isUser = true, content = message)
            )
        }.body()

    suspend fun deleteConversation(conversationId: String): BaseResponse<Boolean> =
        httpClient.delete("${WholeApp.CHAT_BASE_URL}/$CONVERSATION_ROUTE/$conversationId").body()
}
