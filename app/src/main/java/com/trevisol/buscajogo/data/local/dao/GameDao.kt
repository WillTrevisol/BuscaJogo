package com.trevisol.buscajogo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trevisol.buscajogo.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE isWishlist = 1")
    fun getWishlistGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isOwned = 1")
    fun getOwnedGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Int): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGame(game: GameEntity): Long

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Int): Int
}
