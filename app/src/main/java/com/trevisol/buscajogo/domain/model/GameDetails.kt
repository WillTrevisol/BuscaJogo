package com.trevisol.buscajogo.domain.model

data class GameDetails(
    val id: Int,
    val title: String,
    val description: String,
    val bannerUrl: String?,
    val score: Int?,
    val genres: List<String>,
    val offers: List<Offer>
)

data class Offer(
    val storeName: String,
    val price: String?,
    val platforms: List<String>,
    val storeUrl: String
)
