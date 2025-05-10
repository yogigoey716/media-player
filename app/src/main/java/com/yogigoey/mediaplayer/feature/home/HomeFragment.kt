package com.yogigoey.mediaplayer.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yogigoey.mediaplayer.core.network.Resource
import com.yogigoey.mediaplayer.databinding.FragmentHomeBinding
import com.yogigoey.mediaplayer.feature.home.adapter.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogigoey.mediaplayer.R
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMusic()
        setupView()
        observe()
        observeStateFlows()
    }

    private fun observe() = binding.apply {
        viewModel.music.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val songList = it.model?.list ?: emptyList()
                    viewModel.setSongs(songList)

                    rvRecords.adapter = PlaylistAdapter(songList) { position ->
                        viewModel.playSongAt(position)
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorData.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.NetworkError -> {
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Unauthorized -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        btnPlay.setOnClickListener { viewModel.playPauseToggle() }
        btnNext.setOnClickListener { viewModel.next() }
        btnPrevious.setOnClickListener { viewModel.previous() }



    }

    private fun observeStateFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.progress.collect { progress ->
                        if (!viewModel.isUserSeeking) {
                            binding.seekBar.progress = progress
                        }
                    }
                }

                launch {
                    viewModel.isPlaying.collect { isPlaying ->
                        binding.btnPlay.setImageResource(
                            if (isPlaying) {
                                R.drawable.ic_pause
                            }
                            else {

                                R.drawable.ic_play
                            }
                        )
                    }
                }

                launch {
                    viewModel.currentSong.collect { song ->
                        song?.let {
                        }
                    }
                }
            }
        }
    }



    private fun setupView() = binding.apply {
        rvRecords.layoutManager = LinearLayoutManager(requireContext())

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.setSeeking(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.setSeeking(false)
                seekBar?.progress?.let {
                    viewModel.seekTo(it)
                }
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val adapter = rvRecords.adapter as? PlaylistAdapter
                if (newText.isNullOrBlank()) {
                    viewModel.getMusic()
                } else {
                    adapter?.filter?.filter(newText)
                }

                return true
            }
        })

    }







}