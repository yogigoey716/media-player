package com.yogigoey.mediaplayer.feature.home.domain.usecase

import com.yogigoey.mediaplayer.core.FlowUseCase
import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.feature.home.data.repository.MusicRepository
import com.yogigoey.mediaplayer.feature.home.domain.MusicList
import kotlinx.coroutines.flow.Flow

class GetMusicUseCase(
    private val musicRepository: MusicRepository
) : FlowUseCase<Unit, MusicList>() {
    override suspend fun execute(params: Unit?): Flow<Resource<MusicList>> {
        return musicRepository.getSongs()
    }
}