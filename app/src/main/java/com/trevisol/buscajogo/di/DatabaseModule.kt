package com.trevisol.buscajogo.di

import android.content.Context
import androidx.room.Room
import com.trevisol.buscajogo.data.local.BuscaJogoDatabase
import com.trevisol.buscajogo.data.local.dao.GameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BuscaJogoDatabase {
        return Room.databaseBuilder(
            context,
            BuscaJogoDatabase::class.java,
            "buscajogo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: BuscaJogoDatabase): GameDao {
        return database.gameDao()
    }
}
