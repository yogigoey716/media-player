package com.yogigoey.mediaplayer.feature.home.data.repository

import com.google.gson.Gson
import com.yogigoey.mediaplayer.core.network.NetworkResource
import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.feature.home.data.MusicDataResponse
import com.yogigoey.mediaplayer.feature.home.data.service.MusicService
import com.yogigoey.mediaplayer.feature.home.data.toMusic
import com.yogigoey.mediaplayer.feature.home.domain.MusicList
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class MusicRepositoryImpl (
    private val service : MusicService
) : MusicRepository{
    override suspend fun getSongs(): Flow<Resource<MusicList>> {
        return  object : NetworkResource<MusicDataResponse, MusicList>() {
            override suspend fun remoteFetch(): Response<MusicDataResponse> {
                val mockJson = """
                    {
                      "status": 0,
                      "message": "Success",
                      "data": [
                        {
                          "id": "1",
                          "song_cover": "https://lastfm.freetls.fastly.net/i/u/ar0/e38e5f2fb9196e0ce1a44877d5303372.jpg",
                          "song_name": "Siapkah Kau 'Tuk Jatuh Cinta",
                          "artist_name": "HIVI!",
                          "album_name": "HIVI!",
                          "song_url": "audio1.mp3"
                        },
                        {
                          "id": "2",
                          "song_cover": "https://lastfm.freetls.fastly.net/i/u/ar0/e38e5f2fb9196e0ce1a44877d5303372.jpg",
                          "song_name": "Semenjak ada dirimu",
                          "artist_name": "HIVI!",
                          "album_name": "HIVI!",
                          "song_url": "audio2.mp3"
                        }
                      ]
                    }
                """.trimIndent()

                val gson = Gson()
                val dataResponse = gson.fromJson(mockJson, MusicDataResponse::class.java)

                return Response.success(dataResponse)
            }

            override suspend fun mapData(data: MusicDataResponse): MusicList {
                return data.toMusic()
            }

        }.asFlow()
    }
}