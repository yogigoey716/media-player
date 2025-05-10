package com.yogigoey.mediaplayer.modules

import com.yogigoey.mediaplayer.feature.home.domain.usecase.GetMusicUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single{GetMusicUseCase(get())}
}