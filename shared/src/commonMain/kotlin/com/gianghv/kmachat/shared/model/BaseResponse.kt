package com.gianghv.kmachat.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T : Any?> (
    @SerialName("statusCode") val code: Int,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null,
) {
    fun isSuccessful() = code in 200..299
    fun hasSuccessfulData() = isSuccessful() && data != null
    fun getSuccessfulData(): T {
        if (hasSuccessfulData()) return data!!
        else throw Exception("ApiResponse Data is null. " +
                "Make sure you checked value of  hasSuccessfulData() function before getting data")
    }
}