package com.example.network.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorite_cats")

@Singleton
class FavoriteCatsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorites")
    }

    val favorites: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun toggleFavorite(catId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = if (current.contains(catId)) {
                current - catId
            } else {
                current + catId
            }
        }
    }
}
