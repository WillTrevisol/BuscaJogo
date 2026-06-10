package com.trevisol.buscajogo.data.remote.model

import com.google.gson.annotations.SerializedName

data class RawgResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

data class RawgGameDto(
    val id: Int,
    val slug: String,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
    val metacritic: Int?
)
