package com.trevisol.buscajogo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String?,
    val rating: Double,
    val metacritic: Int?,
    val genres: String, // Stored as comma-separated string for simplicity
    val isWishlist: Boolean = false,
    val isOwned: Boolean = false
)
