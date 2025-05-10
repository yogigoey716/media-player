package com.yogigoey.mediaplayer.core.network

import com.yogigoey.mediaplayer.core.data.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import retrofit2.Response

abstract class NetworkResource<T : BaseResponse, Y : BaseModel> {
    fun asFlow() = flow {
        emit(Resource.Loading)
        if (!shouldFetchFromRemote()) {
            localFetch()?.let {
                emit(Resource.Success(it))
                return@flow
            }
        }

        val remoteResponse = safeApiCall(dispatcher = Dispatchers.IO) { remoteFetch() }

        when (remoteResponse) {

            is ResultWrapper.Success -> {
                val remoteData = remoteResponse.data.body()!!
                if (shouldFetchRemoteAndSaveLocal()) {
                    saveLocal(remoteData)
                }
                emit(Resource.Success(mapData(remoteData)))
            }

            is ResultWrapper.Error -> {
                if (remoteResponse.code == NetworkCodes.TIMEOUT_ERROR || remoteResponse.code == NetworkCodes.CONNECTION_ERROR) {
                    val localData = localFetch()
                    if (localData != null) {
                        emit(Resource.Success(localData))
                    } else {
                        emit(Resource.Error(ErrorData(message = "Request timed out, and no local data available.")))
                    }
                }else{
                    emit(Resource.Error(ErrorData(message = remoteResponse.message)))
                }

            }

            is ResultWrapper.Unauthorized -> {
                if (shouldAddActionOnError()){
                    actionOnError()
                    emit(Resource.Success())
                }else{
                    emit(Resource.Unauthorized(ErrorData(message = remoteResponse.message)))
                }
            }

            is ResultWrapper.NetworkError -> {
                if (shouldAddActionOnError()){
                    actionOnError()
                    emit(Resource.Success())
                }else{
                    emit(Resource.NetworkError(ErrorData(message = remoteResponse.message)))
                }
            }
        }

    }


    abstract suspend fun remoteFetch(): Response<T>
    abstract suspend fun mapData(data: T): Y
    open suspend fun saveLocal(data: T) {}
    open suspend fun actionOnError() {}
    open suspend fun localFetch(): Y? = null
    open fun shouldFetchFromRemote() = true
    open fun shouldFetchLocalOnError() = false
    open fun shouldFetchRemoteAndSaveLocal() = false
    open fun shouldAddActionOnError() = false
}