package com.trevisol.buscajogo.di

import com.trevisol.buscajogo.data.remote.api.ItadApi
import com.trevisol.buscajogo.data.remote.api.RawgApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("RawgRetrofit")
    fun provideRawgRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("ItadRetrofit")
    fun provideItadRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.isthereanydeal.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRawgApi(@Named("RawgRetrofit") retrofit: Retrofit): RawgApi {
        return retrofit.create(RawgApi::class.java)
    }

    @Provides
    @Singleton
    fun provideItadApi(@Named("ItadRetrofit") retrofit: Retrofit): ItadApi {
        return retrofit.create(ItadApi::class.java)
    }
}
