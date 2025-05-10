package com.yogigoey.mediaplayer.feature.home.data.repository

import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.feature.home.domain.Music
import com.yogigoey.mediaplayer.feature.home.domain.MusicList
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSongs(): Flow<Resource<MusicList>>
}