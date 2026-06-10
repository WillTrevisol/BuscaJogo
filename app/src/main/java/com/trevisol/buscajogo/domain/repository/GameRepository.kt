package com.trevisol.buscajogo.domain.repository

import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.model.GameDetails

interface GameRepository {
    suspend fun getPopularGames(): Result<List<Game>>
    suspend fun getBestDeals(): Result<List<Deal>>
    suspend fun getGameDetails(id: Int): Result<GameDetails>
}
