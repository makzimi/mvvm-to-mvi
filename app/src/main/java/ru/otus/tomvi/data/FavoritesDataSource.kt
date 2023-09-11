package ru.otus.tomvi.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoritesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun consumeFavoriteIds(): Flow<List<Long>> = dataStore.data
        .map(::mapFromPrefs)

    suspend fun saveFavoriteId(id: Long) {
        dataStore.edit { prefs ->
            val currentIds = mapFromPrefs(prefs).toMutableSet()
            currentIds.add(id)
            prefs[preferencesKey] = Json.encodeToString(currentIds.toList())
        }
    }

    suspend fun removeFavoriteId(id: Long) {
        dataStore.edit { prefs ->
            val currentIds = mapFromPrefs(prefs).toMutableSet()
            currentIds.remove(id)
            prefs[preferencesKey] = Json.encodeToString(currentIds.toList())
        }
    }

    private fun mapFromPrefs(prefs: Preferences): List<Long> =
        prefs[preferencesKey]
            ?.takeIf(String::isNotEmpty)
            ?.let { Json.decodeFromString(it) }
            ?: listOf()

    private val preferencesKey = stringPreferencesKey(KEY)

    private companion object {
        const val KEY = "key"
    }
}
