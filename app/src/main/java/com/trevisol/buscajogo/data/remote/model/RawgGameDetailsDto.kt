package com.trevisol.buscajogo.data.remote.model

import com.google.gson.annotations.SerializedName

data class RawgGameDetailsDto(
    val id: Int,
    val slug: String,
    val name: String,
    @SerializedName("description_raw")
    val descriptionRaw: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val metacritic: Int?,
    val genres: List<RawgGenreDto>,
    val stores: List<RawgStoreLinkDto>?
)

data class RawgStoreLinkDto(
    val id: Int,
    val url: String,
    val store: RawgStoreDto
)

data class RawgStoreDto(
    val id: Int,
    val name: String,
    val slug: String
)

data class RawgPurchaseLinkDto(
    val id: Int,
    @SerializedName("store_id")
    val storeId: Int,
    val url: String
)

data class RawgGenreDto(
    val id: Int,
    val name: String,
    val slug: String
)
