package com.yogigoey.mediaplayer.feature.home

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.feature.home.domain.Music
import com.yogigoey.mediaplayer.feature.home.domain.MusicList
import com.yogigoey.mediaplayer.feature.home.domain.usecase.GetMusicUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getMusicUseCase: GetMusicUseCase,
    private val application: Application
) : AndroidViewModel(application) {


    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _player = ExoPlayer.Builder(context).build()
    val player: ExoPlayer get() = _player

    private var currentIndex = 0
    private var songList: List<Music> = emptyList()

    private val _currentSong = MutableStateFlow<Music?>(null)
    val currentSong: StateFlow<Music?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    var isUserSeeking = false


    val music = MutableLiveData<Resource<MusicList>>()
    val loading = MutableLiveData<Boolean>()
    val musicList = MutableLiveData<MusicList>()
    val error = MutableLiveData<String>()


    init {
        observePlayerEvents()
        updateProgress()
    }

    fun getMusic() = viewModelScope.launch {
        getMusicUseCase().collect {
            music.value = it
            when (it) {
                is Resource.Loading -> {
                    loading.value = true
                }

                is Resource.Success -> {
                    loading.value = false
                    musicList.value = it.model
                }

                is Resource.Error -> {
                    loading.value = false

                    error.value = it.errorData.message

                }

                is Resource.NetworkError -> loading.value = true

                is Resource.Unauthorized -> loading.value = true

            }
        }
    }

    fun setSongs(list: List<Music>) {
        songList = list
    }
    fun playSongAt(index: Int) {
        currentIndex = index
        val song = songList.getOrNull(index) ?: return
        _currentSong.value = song

        val uri = getSongUri(song.filePath)
        uri?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
        }
    }

    fun playPauseToggle() {
        if (player.isPlaying) {player.pause()
        }
        else {player.play()}
    }

    fun next() {
        val nextIndex = (currentIndex + 1) % songList.size
        playSongAt(nextIndex)
    }

    fun previous() {
        val prevIndex = if (currentIndex - 1 < 0) songList.lastIndex else currentIndex - 1
        playSongAt(prevIndex)
    }

    fun seekTo(position: Int) {
        player.seekTo(position.toLong())
        if (!player.isPlaying) {
            player.play()
        }
    }

    fun setSeeking(seeking: Boolean) {
        isUserSeeking = seeking
    }

    private fun getSongUri(path: String): Uri? {
        return if (path.endsWith(".mp3")) {
            val resName = path.substringBeforeLast(".")
            val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
            if (resId != 0) Uri.parse("android.resource://${context.packageName}/$resId") else null
        } else {
            Uri.parse(path)
        }
    }

    private fun observePlayerEvents() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    private fun updateProgress() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                if (player.isPlaying) {
                    val currentPosition = player.currentPosition
                    val duration = player.duration

                    if (duration > 0) {
                        _progress.value = ((currentPosition * 100) / duration).toInt()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}

