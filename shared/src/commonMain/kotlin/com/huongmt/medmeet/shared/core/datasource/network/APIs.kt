package com.huongmt.medmeet.shared.core.datasource.network

import com.huongmt.medmeet.shared.base.BaseResponse
import com.huongmt.medmeet.shared.config.BASE_URL
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.request.BookAppointment
import com.huongmt.medmeet.shared.core.datasource.network.request.CreateConversationRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.SendMessageRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.SignUpRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.UpdateHealthRequest
import com.huongmt.medmeet.shared.core.datasource.network.request.UpdateProfileRequest
import com.huongmt.medmeet.shared.core.datasource.network.response.AppNotificationResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.AppointmentBookingResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicListResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicScheduleResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ConversationResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.HealthRecordResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.LoginResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.MedicalConsultationResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.MessageResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.PagingConsultationResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.PagingMedicalServiceResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.ProfileResponse
import com.huongmt.medmeet.shared.core.datasource.network.response.RecommendAiQuery
import com.huongmt.medmeet.shared.core.datasource.network.response.RecommendService
import com.huongmt.medmeet.shared.core.datasource.network.response.UpdateHealthRecord
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
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

    suspend fun getProfileById(id: String): BaseResponse<ProfileResponse> =
        httpClient.get(BASE_URL + USER_API_ROUTE + id).body()

    suspend fun updateProfile(id: String, request: UpdateProfileRequest): BaseResponse<ProfileResponse> =
        httpClient.put("$BASE_URL$USER_API_ROUTE$id/update") {
            setBody(request)
        }.body()

    suspend fun getAppNotification(userId: String): BaseResponse<List<AppNotificationResponse>> =
        httpClient.get(BASE_URL + "notification/$userId").body()

    suspend fun getHealthRecordByUserId(userId: String): BaseResponse<HealthRecordResponse> =
        httpClient.get("$BASE_URL$HEALTH_RECORD_API_ROUTE/$userId").body()

    suspend fun updateHealthRecord(userId: String, request: UpdateHealthRequest): BaseResponse<HealthRecordResponse> =
        httpClient.put("$BASE_URL$HEALTH_RECORD_API_ROUTE/$userId") {
            setBody(request)
        }.body()

    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): BaseResponse<ProfileResponse> =
        httpClient.put(BASE_URL + USER_API_ROUTE + WholeApp.USER_ID + "/update") {
            setBody(updateProfileRequest)
        }.body()

    suspend fun updateAvatar(userId: String, fileData: ByteArray, fileName: String, mimeType: String = "image/jpeg"): BaseResponse<ProfileResponse> =
        httpClient.submitFormWithBinaryData(
            url = "$BASE_URL$USER_API_ROUTE$userId/update-avatar",
            formData = formData {
                append("file", fileData, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ) {
            method = HttpMethod.Put
        }.body()

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

    suspend fun getClinicSchedule(clinicId: String): BaseResponse<List<ClinicScheduleResponse>> =
        httpClient.get("${BASE_URL}clinic-schedule/$clinicId").body()

    suspend fun getMedicalHistory(
        uid: String,
        page: Int = 1,
        size: Int = 30
    ): BaseResponse<PagingConsultationResponse> =
        httpClient.get("${BASE_URL}medical-consultation-history") {
            parameter("patientId", uid)
            parameter("_page", page)
            parameter("_pageSize", size)
        }.body()

    suspend fun getMedicalHistoryDetail(
        appointmentId: String
    ): BaseResponse<MedicalConsultationResponse> =
        httpClient.get("${BASE_URL}medical-consultation-history/$appointmentId").body()

    suspend fun getMedicalServices(
        clinicId: String,
        page: Int = 1,
        size: Int = 30
    ): BaseResponse<PagingMedicalServiceResponse> = httpClient.get("${BASE_URL}medical-service") {
        parameter("clinicId", clinicId)
        parameter("_page", page)
        parameter("_pageSize", size)
    }.body()

    suspend fun bookAppointment(bookingRequest: BookAppointment): BaseResponse<AppointmentBookingResponse> =
        httpClient.post("${BASE_URL}medical-consultation-history") {
            setBody(bookingRequest)
        }.body()

    suspend fun cancelAppointment(appointmentId: String): BaseResponse<AppointmentBookingResponse> =
        httpClient.put("${BASE_URL}medical-consultation-history/$appointmentId/cancel").body()

    suspend fun getMedicalServiceSchedule(
        medicalServiceId: String,
        date: String
    ): BaseResponse<List<ClinicScheduleResponse>> =
        httpClient.get("${BASE_URL}medical-service/$medicalServiceId/schedules") {
            parameter("date", date)
        }.body()

//    Chat APIs

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

    suspend fun getRecommendQuery(conversationId: String): BaseResponse<RecommendAiQuery> =
        httpClient.post("${WholeApp.CHAT_BASE_URL}/api/conversations/$conversationId/recommend").body()

    suspend fun getRecommendHealthService(conversationId: String): BaseResponse<RecommendService> =
        httpClient.post("${WholeApp.CHAT_BASE_URL}/api/conversations/$conversationId/service-recommendations").body()
}
