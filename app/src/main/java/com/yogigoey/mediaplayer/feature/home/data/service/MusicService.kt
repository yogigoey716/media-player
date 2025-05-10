package com.yogigoey.mediaplayer.feature.home.data.service

import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.feature.home.data.MusicDataResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET

interface MusicService{
    @GET("songs_data.json")
    fun getSongs(): Response<MusicDataResponse>
}