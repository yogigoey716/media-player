package com.yogigoey.mediaplayer.modules

import com.yogigoey.mediaplayer.feature.home.data.service.MusicService
import org.koin.dsl.module
import retrofit2.Retrofit


val serviceModule = module{
    factory { get<Retrofit>().create(MusicService::class.java) }
}