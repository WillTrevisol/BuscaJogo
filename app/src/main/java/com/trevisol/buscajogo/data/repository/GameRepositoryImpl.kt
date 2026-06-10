package com.trevisol.buscajogo.data.repository

import com.trevisol.buscajogo.BuildConfig
import com.trevisol.buscajogo.data.remote.api.ItadApi
import com.trevisol.buscajogo.data.remote.api.RawgApi
import com.trevisol.buscajogo.data.remote.model.RawgGameDetailsDto
import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.model.GameDetails
import com.trevisol.buscajogo.domain.model.Offer
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
                    platforms = item.deal.platforms?.map { it.name } ?: emptyList()
                )
            }
            Result.success(deals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameDetails(id: Int): Result<GameDetails> {
        return try {
            val rawgDetails = rawgApi.getGameDetails(id, BuildConfig.RAWG_API_KEY)
            val rawgStoreLinks = rawgApi.getGameStoreLinks(id, BuildConfig.RAWG_API_KEY).results
            
            // 1. Try strict lookup
            val itadLookup = itadApi.lookupGame(BuildConfig.IS_THERE_ANY_DEAL_API_KEY, rawgDetails.name)
            
            val itadId = if (itadLookup.found && itadLookup.game != null) {
                itadLookup.game.id
            } else {
                // 2. Fallback: Fuzzy search by full title
                val itadSearch = itadApi.searchGames(BuildConfig.IS_THERE_ANY_DEAL_API_KEY, rawgDetails.name)
                itadSearch.firstOrNull()?.id ?: run {
                    // 3. Last Fallback: Search by simplified title (remove year like (2018))
                    val simplifiedTitle = rawgDetails.name.replace(Regex("\\s\\(\\d{4}\\)"), "").trim()
                    if (simplifiedTitle != rawgDetails.name) {
                        itadApi.searchGames(BuildConfig.IS_THERE_ANY_DEAL_API_KEY, simplifiedTitle).firstOrNull()?.id
                    } else null
                }
            }

            val itadOffers = if (itadId != null) {
                val itadPrices = itadApi.getGamePrices(
                    apiKey = BuildConfig.IS_THERE_ANY_DEAL_API_KEY,
                    shops = "51,61,35,65",
                    gameIds = listOf(itadId)
                )
                
                val gamePriceInfo = itadPrices.firstOrNull()
                
                gamePriceInfo?.deals?.map { deal ->
                    Offer(
                        storeName = deal.shop.name,
                        price = String.format(Locale.getDefault(), "R$ %.2f", deal.price.amount),
                        platforms = deal.platforms?.map { it.name } ?: emptyList(),
                        storeUrl = deal.url ?: ""
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }

            // Merge Logic: Use RAWG stores as base, enrich with ITAD prices
            val mergedOffers = rawgDetails.stores?.map { rawgStoreMetadata ->
                val matchingItadOffer = itadOffers.find { it.storeName.contains(rawgStoreMetadata.store.name, ignoreCase = true) }
                
                // Get the actual redirect URL for this store
                val redirectUrl = rawgStoreLinks.find { it.storeId == rawgStoreMetadata.store.id }?.url
                    ?: rawgStoreMetadata.url

                Offer(
                    storeName = rawgStoreMetadata.store.name,
                    price = matchingItadOffer?.price,
                    platforms = matchingItadOffer?.platforms ?: emptyList(),
                    storeUrl = matchingItadOffer?.storeUrl?.takeIf { it.isNotEmpty() } ?: redirectUrl
                )
            } ?: itadOffers // Fallback to ITAD if RAWG stores are null

            val details = GameDetails(
                id = rawgDetails.id,
                title = rawgDetails.name,
                description = rawgDetails.descriptionRaw,
                bannerUrl = rawgDetails.backgroundImage,
                score = rawgDetails.metacritic,
                genres = rawgDetails.genres.map { it.name },
                offers = mergedOffers
            )
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
