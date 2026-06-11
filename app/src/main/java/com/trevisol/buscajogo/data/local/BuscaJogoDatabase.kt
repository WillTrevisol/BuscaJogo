package com.trevisol.buscajogo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trevisol.buscajogo.data.local.dao.GameDao
import com.trevisol.buscajogo.data.local.entity.GameEntity

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class BuscaJogoDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
