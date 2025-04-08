package com.gianghv.kmachat.shared.base

sealed class BaseError {
    data object NetworkError : BaseError()
    data object ConnectionTimeout : BaseError()
    data object ServerError : BaseError()
    data class HttpError(val message: String) : BaseError()
    data object SessionExpired : BaseError()
    data class UnknownError(val throwable: Throwable) : BaseError()
    data object JsonConvertException : BaseError()
}

// Define a custom exception class to carry the BaseError information
class ErrorException(val error: BaseError) : Exception(error.toString())
