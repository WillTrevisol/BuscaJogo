package com.trevisol.buscajogo.data.remote.api

import com.trevisol.buscajogo.data.remote.model.ItadDealsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItadApi {
    @GET("deals/v2")
    suspend fun getDeals(
        @Query("key") apiKey: String,
        @Query("country") country: String = "BR",
        @Query("limit") limit: Int = 20,
        @Query("shops") shops: String? = null
    ): ItadDealsResponseDto
}
