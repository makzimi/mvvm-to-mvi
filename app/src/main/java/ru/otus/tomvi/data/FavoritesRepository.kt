package ru.otus.tomvi.data

import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
    private val favoritesDataSource: FavoritesDataSource
) {
    fun consumeFavorites(): Flow<List<Long>> {
        return favoritesDataSource.consumeFavoriteIds()
    }

    suspend fun addToFavorites(id: Long) {
        return favoritesDataSource.saveFavoriteId(id)
    }

    suspend fun removeFromFavorites(id: Long) {
        return favoritesDataSource.removeFavoriteId(id)
    }
}