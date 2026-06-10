package com.trevisol.buscajogo.domain.model

data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double,
    val metacritic: Int?
)
