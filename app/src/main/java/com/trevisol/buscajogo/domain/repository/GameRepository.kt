package com.trevisol.buscajogo.domain.repository

import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game

interface GameRepository {
    suspend fun getPopularGames(): Result<List<Game>>
    suspend fun getBestDeals(): Result<List<Deal>>
}
