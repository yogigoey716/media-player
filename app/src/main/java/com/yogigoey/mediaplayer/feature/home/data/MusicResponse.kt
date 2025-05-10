package com.yogigoey.mediaplayer.feature.home.data

import com.google.gson.annotations.SerializedName
import com.yogigoey.mediaplayer.core.network.BaseResponse

data class MusicResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("song_cover")
    val songCover: String,
    @SerializedName("song_name")
    val songName: String,
    @SerializedName("artist_name")
    val artistName: String,
    @SerializedName("album_name")
    val albumName: String,
    @SerializedName("song_url")
    val songUrl: String
)

data class MusicDataResponse(
    @SerializedName("data")
    val data: List<MusicResponse>,
) : BaseResponse()