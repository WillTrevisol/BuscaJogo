package com.trevisol.buscajogo.data.remote.api

import com.trevisol.buscajogo.data.remote.model.ItadDealsResponseDto
import com.trevisol.buscajogo.data.remote.model.ItadLookupResponseDto
import com.trevisol.buscajogo.data.remote.model.ItadPriceResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface ItadApi {
    @GET("deals/v2")
    suspend fun getDeals(
        @Query("key") apiKey: String,
        @Query("country") country: String = "BR",
        @Query("limit") limit: Int = 20,
        @Query("shops") shops: String? = null,
        @Query("filter") filter: String? = null
    ): ItadDealsResponseDto

    @GET("games/lookup/v1")
    suspend fun lookupGame(
        @Query("key") apiKey: String,
        @Query("title") title: String
    ): ItadLookupResponseDto

    @POST("games/prices/v3")
    suspend fun getGamePrices(
        @Query("key") apiKey: String,
        @Query("country") country: String = "BR",
        @Query("shops") shops: String? = null,
        @Body gameIds: List<String>
    ): List<ItadPriceResponseDto>

    @GET("games/search/v1")
    suspend fun searchGames(
        @Query("key") apiKey: String,
        @Query("title") title: String,
        @Query("results") results: Int = 1
    ): List<com.trevisol.buscajogo.data.remote.model.ItadGameInfoDto>
}
