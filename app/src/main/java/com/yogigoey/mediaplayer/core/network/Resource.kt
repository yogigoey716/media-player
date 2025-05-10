package com.yogigoey.mediaplayer.core.network

sealed class Resource<out T> {
    data class Success<T>(val model: T? = null) : Resource<T>()
    data class Error(val errorData: ErrorData) : Resource<Nothing>()
    data class Unauthorized(val errorData: ErrorData) : Resource<Nothing>()
    data class NetworkError(val errorData: ErrorData) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}