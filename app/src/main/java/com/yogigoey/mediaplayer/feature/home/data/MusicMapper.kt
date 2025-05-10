package com.yogigoey.mediaplayer.feature.home.data

import com.yogigoey.mediaplayer.feature.home.domain.Music
import com.yogigoey.mediaplayer.feature.home.domain.MusicList


fun MusicResponse.toMusic(): Music {
    return Music(
        id = id.toInt(),
        title = songName,
        artist = artistName,
        album = albumName,
        filePath = songUrl,
        coverArt = songCover
    )
}

fun MusicDataResponse.toMusic() : MusicList {
    return MusicList(
        list = data.map { it.toMusic() }
    )
}