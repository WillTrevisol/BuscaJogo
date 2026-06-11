package com.trevisol.buscajogo.domain.repository

import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.model.GameDetails
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun getPopularGames(): Result<List<Game>>
    suspend fun getBestDeals(): Result<List<Deal>>
    suspend fun getGameDetails(id: Int): Result<GameDetails>
    suspend fun searchGames(query: String): Result<List<Game>>
    suspend fun getGameIdByTitle(title: String): Result<Int>

    // Local Data
    suspend fun toggleWishlist(game: GameDetails): Result<Unit>
    suspend fun addToCollection(game: GameDetails): Result<Unit>
    suspend fun removeFromCollection(gameId: Int): Result<Unit>
    fun getWishlist(): Flow<List<Game>>
    fun getCollection(): Flow<List<Game>>
    fun isGameInWishlist(id: Int): Flow<Boolean>
    fun isGameInCollection(id: Int): Flow<Boolean>
}
