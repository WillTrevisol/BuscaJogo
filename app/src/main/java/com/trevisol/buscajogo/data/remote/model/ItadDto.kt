package com.trevisol.buscajogo.data.remote.model

data class ItadDealsResponseDto(
    val nextOffset: Int,
    val hasMore: Boolean,
    val list: List<ItadDealListItemDto>
)

data class ItadDealListItemDto(
    val id: String,
    val title: String,
    val assets: ItadAssetsDto,
    val deal: ItadDealDto
)

data class ItadAssetsDto(
    val boxart: String?,
    val banner145: String?,
    val banner300: String?,
    val banner400: String?,
    val banner600: String?
)

data class ItadDealDto(
    val shop: ItadShopDto,
    val price: ItadPriceDto,
    val regular: ItadPriceDto,
    val cut: Int,
    val platforms: List<ItadPlatformDto>?,
    val url: String?
)

data class ItadShopDto(
    val id: Int,
    val name: String
)

data class ItadPlatformDto(
    val id: Int,
    val name: String
)

data class ItadPriceDto(
    val amount: Double,
    val currency: String
)

data class ItadLookupResponseDto(
    val found: Boolean,
    val game: ItadGameInfoDto?
)

data class ItadGameInfoDto(
    val id: String,
    val title: String
)

data class ItadPriceResponseDto(
    val id: String,
    val deals: List<ItadDealDto>
)
