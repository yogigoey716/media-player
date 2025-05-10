package com.yogigoey.mediaplayer.modules

import com.yogigoey.mediaplayer.feature.home.data.repository.MusicRepository
import com.yogigoey.mediaplayer.feature.home.data.repository.MusicRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MusicRepository>{MusicRepositoryImpl(get())}
}