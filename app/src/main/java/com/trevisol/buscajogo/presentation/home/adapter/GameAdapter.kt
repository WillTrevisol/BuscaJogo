package com.trevisol.buscajogo.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.trevisol.buscajogo.databinding.ItemGameBinding
import com.trevisol.buscajogo.domain.model.Game

class GameAdapter : ListAdapter<Game, GameAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GameViewHolder(private val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.tvGameName.text = game.name
            
            val score = game.metacritic
            if (score != null) {
                binding.tvRating.visibility = android.view.View.VISIBLE
                binding.tvRating.text = score.toString()
                
                val backgroundColor = when {
                    score >= 75 -> androidx.core.content.ContextCompat.getColor(binding.root.context, com.trevisol.buscajogo.R.color.rating_green)
                    score >= 50 -> androidx.core.content.ContextCompat.getColor(binding.root.context, com.trevisol.buscajogo.R.color.rating_yellow)
                    else -> androidx.core.content.ContextCompat.getColor(binding.root.context, com.trevisol.buscajogo.R.color.error)
                }
                binding.tvRating.backgroundTintList = android.content.res.ColorStateList.valueOf(backgroundColor)
            } else {
                binding.tvRating.visibility = android.view.View.GONE
            }

            binding.ivGameImage.load(game.imageUrl) {
                crossfade(true)
            }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem == newItem
    }
}
