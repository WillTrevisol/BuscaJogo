package com.trevisol.buscajogo.data.remote.api

import com.trevisol.buscajogo.data.remote.model.RawgGameDetailsDto
import com.trevisol.buscajogo.data.remote.model.RawgGameDto
import com.trevisol.buscajogo.data.remote.model.RawgResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RawgApi {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("ordering") ordering: String = "-added",
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null
    ): RawgResponse<RawgGameDto>

    @GET("games/{id}")
    suspend fun getGameDetails(
        @retrofit2.http.Path("id") id: Int,
        @Query("key") apiKey: String
    ): RawgGameDetailsDto

    @GET("games/{id}/stores")
    suspend fun getGameStoreLinks(
        @retrofit2.http.Path("id") id: Int,
        @Query("key") apiKey: String
    ): RawgResponse<com.trevisol.buscajogo.data.remote.model.RawgPurchaseLinkDto>

    @GET("games")
    suspend fun getGameByTitle(
        @Query("key") apiKey: String,
        @Query("search") title: String,
        @Query("search_exact") exact: Boolean = true,
        @Query("page_size") pageSize: Int = 1
    ): RawgResponse<RawgGameDto>
}
