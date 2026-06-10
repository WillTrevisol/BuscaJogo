package com.trevisol.buscajogo.domain.model

data class Deal(
    val id: String,
    val title: String,
    val salePrice: String,
    val normalPrice: String,
    val savings: String,
    val thumbnailUrl: String,
    val platforms: List<String>
)
