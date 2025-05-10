package com.yogigoey.mediaplayer.feature.home.domain

import com.yogigoey.mediaplayer.core.data.BaseModel

data class Music (
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val filePath: String,
    val coverArt: String? = null
)

data class MusicList(
    val list : List<Music>
): BaseModel()