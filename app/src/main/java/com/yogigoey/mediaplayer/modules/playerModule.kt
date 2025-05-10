package com.yogigoey.mediaplayer.modules

import androidx.media3.exoplayer.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playerModule = module {
    single{ExoPlayer.Builder(androidContext()).build()}
}