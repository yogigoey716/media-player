package com.yogigoey.mediaplayer.core.network


sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val code: Int, val message: String? = null) : ResultWrapper<Nothing>()
    data class NetworkError(val code: Int, val message: String? = null) : ResultWrapper<Nothing>()
    data class Unauthorized(val code: Int, val message: String? = null) : ResultWrapper<Nothing>()
}
