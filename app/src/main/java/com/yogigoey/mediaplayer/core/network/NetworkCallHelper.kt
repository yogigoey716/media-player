package com.yogigoey.mediaplayer.core.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


internal suspend fun <T : BaseResponse> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> Response<T>
): ResultWrapper<Response<T>> {
    return withContext(dispatcher) {
        try {
            val call = apiCall()
            if (call.isSuccessful) ResultWrapper.Success(call)
            else {
                val errorResponse: BaseResponse? = convertBody(call.errorBody())
                if (errorResponse?.message.isNullOrEmpty()) {
                    throw HttpException(call)
                }
                ResultWrapper.Error(call.code(), errorResponse?.message)
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    ResultWrapper.Error(
                        NetworkCodes.TIMEOUT_ERROR,
                        ErrorCodesMapper.getMessage(NetworkCodes.TIMEOUT_ERROR)
                    )
                }
                is IOException -> {
                    ResultWrapper.Error(
                        NetworkCodes.CONNECTION_ERROR,
                        ErrorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }
                is HttpException -> {
                    when (throwable.code()) {
                        401 -> ResultWrapper.Unauthorized(
                            throwable.code(),
                            throwable.localizedMessage
                        )
                        500, 501, 5002, 5004  ->{
                            ResultWrapper.NetworkError(throwable.code(), throwable.localizedMessage)
                        }
                        else -> ResultWrapper.Error(throwable.code(), throwable.localizedMessage)
                    }

                }
                else -> {
                    ResultWrapper.Error(
                        NetworkCodes.GENERIC_ERROR,
                        throwable.localizedMessage
                    )
                }
            }
        }
    }
}

suspend fun <T> call(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> Response<T>
): ResultWrapper<Response<T>> {
    return withContext(dispatcher) {
        try {
            val call = apiCall()
            if (call.isSuccessful) {
                ResultWrapper.Success(call)
            } else {
                val errorResponse: BaseResponse? = convertBody(call.errorBody())
                if (errorResponse?.message.isNullOrEmpty()) {
                    throw HttpException(call)
                }
                ResultWrapper.Error(call.code(), errorResponse?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is TimeoutCancellationException -> {
                    ResultWrapper.Error(
                        NetworkCodes.TIMEOUT_ERROR,
                        ErrorCodesMapper.getMessage(NetworkCodes.TIMEOUT_ERROR)
                    )
                }

                is IOException -> {
                    ResultWrapper.Error(
                        NetworkCodes.CONNECTION_ERROR,
                        ErrorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }

                is HttpException -> {
                    when (throwable.code()) {
                        401 -> ResultWrapper.Unauthorized(
                            throwable.code(),
                            throwable.localizedMessage
                        )

                        500, 501, 5002, 5004 -> {
                            ResultWrapper.NetworkError(throwable.code(), throwable.localizedMessage)
                        }

                        else -> ResultWrapper.Error(throwable.code(), throwable.localizedMessage)
                    }

                }

                else -> {
                    ResultWrapper.Error(
                        NetworkCodes.GENERIC_ERROR,
                        throwable.localizedMessage
                    )
                }
            }
        }
    }
}

private fun convertBody(response: ResponseBody?): BaseResponse? {
    return try {
        val type = object : TypeToken<BaseResponse>() {}.type
        return Gson().fromJson(response?.charStream(), type)
    } catch (exception: Exception) {
        null
    }
}