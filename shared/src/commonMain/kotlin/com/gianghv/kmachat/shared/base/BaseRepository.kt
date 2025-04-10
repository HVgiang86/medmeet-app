package com.gianghv.kmachat.shared.base

import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

abstract class BaseRepository : KoinComponent {
    private val ioDispatcher: CoroutineDispatcher by inject()

    private fun getContext() = ioDispatcher

    /**
     * Make template code to get data with flow
     * @return a flow of data
     */
    protected suspend fun <R> flowContext(
        context: CoroutineContext = getContext(),
        block: suspend () -> BaseResponse<R>,
    ): Flow<R> = withContext(context) {
        flow {
            try {
                val response = block.invoke()
                if (response.code in 200..299) {
                    if (!response.isSuccessful()) {
                        throw ErrorException(response.toError())
                    }

                    val result = response.getSuccessfulData()
                    println("[DEBUG] $result")

                    emit(result)
                } else {
                    throw ErrorException(response.toError())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw ErrorException(e.toError())
            }
        }
    }

    /**
     * Make template code to get data with flow
     * @return a flow of data
     */
    protected suspend fun <R, T> flowContext(
        context: CoroutineContext = getContext(),
        block: suspend () -> BaseResponse<R>,
        mapper: (R) -> T,
    ): Flow<T> = withContext(context) {
        flow {
            try {
                val response = block.invoke()
                if (response.code in 200..299) {
                    if (!response.isSuccessful()) {
                        throw ErrorException(response.toError())
                    }

                    val result = response.mapDataOnSuccess(mapper)
                    println("[DEBUG] $result")

                    emit(result)
                } else {
                    throw ErrorException(response.toError())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw ErrorException(e.toError())
            }
        }
    }

    protected suspend fun returnIfSuccess(
        context: CoroutineContext = getContext(),
        block: suspend () -> BaseResponse<*>,
    ): Flow<Boolean> = withContext(context) {
        flow {
            try {
                val response = block.invoke()
                if (response.code in 200..299) {
                    if (!response.isSuccessful()) {
                        throw ErrorException(response.toError())
                    }

                    val result = response.getSuccessfulData()
                    println("[DEBUG] $result")

                    emit(true)
                } else {
                    throw ErrorException(response.toError())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw ErrorException(e.toError())
            }
        }
    }
}

/**
 * Extension function to map BaseResponse to BaseError
 */
fun BaseResponse<*>.toError(): BaseError = when (this.code) {
    401 -> BaseError.SessionExpired
    in 400..499 -> BaseError.HttpError(this.message ?: "Unknown HTTP Error")
    in 500..599 -> BaseError.ServerError
    else -> {
        Napier.e("Unknown error: [${this.code}] ${this.message}")
        BaseError.UnknownError(Exception(this.message ?: "Unknown error"))
    }
}

/**
 * Extension function to map ResponseException to BaseError
 */
fun ResponseException.toError(): BaseError = when (this.response.status.value) {
    401 -> BaseError.SessionExpired
    in 400..499 -> BaseError.HttpError("${this.response.status.value}: ${this.message ?: "Unknown HTTP Error"}")
    in 500..599 -> BaseError.ServerError
    else -> {
        Napier.e("Unknown error: [${this.response.status.value}] ${this.message}")
        BaseError.UnknownError(Exception(this.message ?: "Unknown error"))
    }
}

/**
 * Extension function to map generic Exception to BaseError
 */
fun Exception.toError(): BaseError = when (this) {
    is JsonConvertException, is SerializationException -> BaseError.JsonConvertException
    is ConnectTimeoutException, is SocketTimeoutException, is HttpRequestTimeoutException -> BaseError.ConnectionTimeout
    is UnresolvedAddressException -> BaseError.NetworkError
    else -> {
        if ((this.message ?: "").contains("NoRouteToHostException", ignoreCase = true)) {
            BaseError.NetworkError
        } else {
            Napier.e("Unknown error: ${this.message}")
            BaseError.UnknownError(this)
        }
    }
}
