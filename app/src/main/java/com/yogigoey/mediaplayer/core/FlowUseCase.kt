package com.yogigoey.mediaplayer.core


import com.yogigoey.mediaplayer.core.network.ErrorData
import com.yogigoey.mediaplayer.core.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

abstract class FlowUseCase<in P, out R>() {
    suspend operator fun invoke(parameters: P? = null): Flow<Resource<R>> = execute(parameters)
        .catch { e ->
            Resource.Error(
                ErrorData(
                    message = e.localizedMessage
                )
            )
        }

    protected abstract suspend fun execute(parameters: P? = null): Flow<Resource<R>>
}
