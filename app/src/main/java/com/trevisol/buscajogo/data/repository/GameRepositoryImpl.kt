package com.trevisol.buscajogo.data.repository

import com.trevisol.buscajogo.BuildConfig
import com.trevisol.buscajogo.data.local.dao.GameDao
import com.trevisol.buscajogo.data.local.entity.GameEntity
import com.trevisol.buscajogo.data.remote.api.ItadApi
import com.trevisol.buscajogo.data.remote.api.RawgApi
import com.trevisol.buscajogo.data.remote.model.RawgGameDetailsDto
import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.model.GameDetails
import com.trevisol.buscajogo.domain.model.Offer
import com.trevisol.buscajogo.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val rawgApi: RawgApi,
    private val itadApi: ItadApi,
    private val gameDao: GameDao
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
            // Filter by quality: at least 70% Metacritic score
            val response = itadApi.getDeals(
                apiKey = BuildConfig.IS_THERE_ANY_DEAL_API_KEY,
                shops = "51,61,35,65",
                filter = "{\"metaCritic\":{\"min\":80,\"max\":100}}"
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

    override suspend fun searchGames(query: String): Result<List<Game>> {
        return try {
            val response = rawgApi.getGames(
                apiKey = BuildConfig.RAWG_API_KEY,
                search = query
            )
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

    override suspend fun getGameIdByTitle(title: String): Result<Int> {
        return try {
            // Clean title for better matching
            val cleanedTitle = title
                .replace(Regex("(?i)Deluxe|Ultimate|Standard|Gold|Edition|DLC|Pack|Bundle"), "")
                .replace(Regex("[^a-zA-Z0-9\\s]"), " ")
                .trim()
                .replace(Regex("\\s+"), " ")

            val response = rawgApi.getGameByTitle(
                apiKey = BuildConfig.RAWG_API_KEY,
                title = if (cleanedTitle.length > 3) cleanedTitle else title
            )
            val gameId = response.results.firstOrNull()?.id
            if (gameId != null) {
                Result.success(gameId)
            } else {
                Result.failure(Exception("Jogo não encontrado na RAWG"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleWishlist(game: GameDetails): Result<Unit> {
        return try {
            val existing = gameDao.getGameById(game.id)
            if (existing != null) {
                if (existing.isWishlist) {
                    // Remove from wishlist
                    if (existing.isOwned) {
                        gameDao.upsertGame(existing.copy(isWishlist = false))
                    } else {
                        gameDao.deleteGameById(game.id)
                    }
                } else {
                    // Add to wishlist
                    gameDao.upsertGame(existing.copy(isWishlist = true))
                }
            } else {
                gameDao.upsertGame(game.toEntity(isWishlist = true))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToCollection(game: GameDetails): Result<Unit> {
        return try {
            val existing = gameDao.getGameById(game.id)
            if (existing != null) {
                gameDao.upsertGame(existing.copy(isOwned = true, isWishlist = false))
            } else {
                gameDao.upsertGame(game.toEntity(isOwned = true))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCollection(gameId: Int): Result<Unit> {
        return try {
            val existing = gameDao.getGameById(gameId)
            if (existing != null) {
                if (existing.isWishlist) {
                    // Just remove from collection, keep in wishlist
                    gameDao.upsertGame(existing.copy(isOwned = false))
                } else {
                    // Not in wishlist, delete from DB
                    gameDao.deleteGameById(gameId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWishlist(): Flow<List<Game>> {
        return gameDao.getWishlistGames().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCollection(): Flow<List<Game>> {
        return gameDao.getOwnedGames().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isGameInWishlist(id: Int): Flow<Boolean> {
        return gameDao.getWishlistGames().map { list ->
            list.any { it.id == id }
        }.distinctUntilChanged()
    }

    override fun isGameInCollection(id: Int): Flow<Boolean> {
        return gameDao.getOwnedGames().map { list ->
            list.any { it.id == id }
        }.distinctUntilChanged()
    }

    private fun GameDetails.toEntity(isWishlist: Boolean = false, isOwned: Boolean = false): GameEntity {
        return GameEntity(
            id = id,
            title = title,
            imageUrl = bannerUrl,
            rating = 0.0, // We don't have it here but we have metacritic
            metacritic = score,
            genres = genres.joinToString(","),
            isWishlist = isWishlist,
            isOwned = isOwned
        )
    }

    private fun GameEntity.toDomain(): Game {
        return Game(
            id = id,
            name = title,
            imageUrl = imageUrl,
            rating = rating,
            metacritic = metacritic
        )
    }
}
