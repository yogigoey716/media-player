package com.yogigoey.mediaplayer.feature.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yogigoey.mediaplayer.R
import com.yogigoey.mediaplayer.feature.home.domain.Music

class PlaylistAdapter(
    private val data: List<Music>,
    private val onItemClick : (Int) -> Unit,
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>(), Filterable {

    private var playlists = data.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            val realIndex = data.indexOf(playlists[position])
            onItemClick(realIndex)
        }
    }

    override fun getItemCount(): Int = playlists.size



    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playlist: Music) {
           val img = itemView.findViewById<ImageView>(R.id.iv_playlist)
            val recordName = itemView.findViewById<TextView>(R.id.tv_record_name)
            val artist = itemView.findViewById<TextView>(R.id.tv_artist)
            val recordAlbum = itemView.findViewById<TextView>(R.id.tv_record_album)

            img.load(playlist.coverArt?.toUri()) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.error_icon)
            }
            recordName.text = playlist.title
            artist.text = playlist.artist
            recordAlbum.text = playlist.album
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filtered = if (query.isNullOrBlank()) {
                    playlists
                } else {
                    playlists.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.artist.contains(query, ignoreCase = true) ||
                                it.album.contains(query, ignoreCase = true)
                    }
                }

                return FilterResults().apply {
                    values = filtered
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                playlists = (results?.values as? List<Music>)?.toMutableList() ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }
}