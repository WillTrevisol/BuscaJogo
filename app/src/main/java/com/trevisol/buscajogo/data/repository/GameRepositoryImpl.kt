package com.trevisol.buscajogo.data.repository

import com.trevisol.buscajogo.BuildConfig
import com.trevisol.buscajogo.data.remote.api.ItadApi
import com.trevisol.buscajogo.data.remote.api.RawgApi
import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.repository.GameRepository
import java.util.Locale
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val rawgApi: RawgApi,
    private val itadApi: ItadApi
) : GameRepository {

    override suspend fun getPopularGames(): Result<List<Game>> {
        return try {
            val response = rawgApi.getGames(apiKey = BuildConfig.RAWG_API_KEY)
            val games = response.results.map { dto ->
                Game(
                    id = dto.id,
                    name = dto.name,
                    imageUrl = dto.backgroundImage,
                    rating = dto.rating,
                    metacritic = dto.metacritic
                )
            }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBestDeals(): Result<List<Deal>> {
        return try {
            // Filter by well-known shops: Nuuvem(51), Steam(61), GOG(35), Epic(65)
            val response = itadApi.getDeals(
                apiKey = BuildConfig.IS_THERE_ANY_DEAL_API_KEY,
                shops = "51,61,35,65"
            )
            val deals = response.list.map { item ->
                Deal(
                    id = item.id,
                    title = item.title,
                    salePrice = String.format(Locale.getDefault(), "%.2f", item.deal.price.amount),
                    normalPrice = String.format(Locale.getDefault(), "%.2f", item.deal.regular.amount),
                    savings = item.deal.cut.toString(),
                    thumbnailUrl = item.assets.boxart
                        ?: item.assets.banner600
                        ?: item.assets.banner400
                        ?: item.assets.banner300
                        ?: item.assets.banner145
                        ?: "",
                    platforms = item.deal.platforms.map { it.name }
                )
            }
            Result.success(deals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
