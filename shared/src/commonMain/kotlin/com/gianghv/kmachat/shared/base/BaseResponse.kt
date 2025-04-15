package com.gianghv.kmachat.shared.base

import io.github.aakira.napier.Napier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T : Any?>(
    @SerialName("statusCode") val code: Int,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null
) {
    fun isSuccessful() = code in 200..299

    fun hasSuccessfulData() = isSuccessful() && data != null

    fun getSuccessfulData(): T {
        if (hasSuccessfulData()) {
            return data!!
        } else {
            throw Exception(
                "ApiResponse Data is null. " + "Make sure you checked value of  hasSuccessfulData() function before getting data"
            )
        }
    }
}

inline fun <T : Any?, R> BaseResponse<T>.mapDataOnSuccess(transform: (T) -> R): R {
    if (isSuccessful()) {
        return try {
            transform(getSuccessfulData())
        } catch (e: Exception) {
            Napier.e("Map data error", e)
            throw MapDataException(
                message = "Map data error",
                cause = e
            )
        }
    } else {
        throw MapDataException(
            message = "Map data error",
            cause =
            Exception(
                "ApiResponse Data is null. " + "Make sure you checked value of  hasSuccessfulData() function before getting data"
            )
        )
    }
}
