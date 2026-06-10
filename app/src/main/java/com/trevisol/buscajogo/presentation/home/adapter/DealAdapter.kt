package com.trevisol.buscajogo.presentation.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.trevisol.buscajogo.R
import com.trevisol.buscajogo.databinding.ItemDealBinding
import com.trevisol.buscajogo.domain.model.Deal

class DealAdapter : ListAdapter<Deal, DealAdapter.DealViewHolder>(DealDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val binding = ItemDealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DealViewHolder(private val binding: ItemDealBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deal: Deal) {
            binding.tvDealTitle.text = deal.title
            binding.tvSalePrice.text = "R$ ${deal.salePrice}"
            binding.tvNormalPrice.text = "R$ ${deal.normalPrice}"
            binding.tvNormalPrice.paintFlags = binding.tvNormalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvSavings.text = "-${deal.savings}%"
            binding.ivDealImage.load(deal.thumbnailUrl) {
                crossfade(true)
            }

            // Clear previous platforms
            binding.llPlatforms.removeAllViews()

            // Add platform tags
            deal.platforms.forEach { platform ->
                val tvPlatform = TextView(binding.root.context).apply {
                    text = platform
                    textSize = 10f
                    setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant))
                    setPadding(8, 2, 8, 2)
                    background = ContextCompat.getDrawable(context, R.drawable.bg_tag)
                }
                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                }
                binding.llPlatforms.addView(tvPlatform, params)
            }
        }
    }

    class DealDiffCallback : DiffUtil.ItemCallback<Deal>() {
        override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem == newItem
    }
}
