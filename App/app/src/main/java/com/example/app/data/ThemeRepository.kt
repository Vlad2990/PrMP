package com.example.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app.domain.interfaces.ThemeRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class ThemeRepository(
    private val dataStore: DataStore<Preferences>,
    private val firestore: FirebaseFirestore
) : ThemeRepositoryInterface {
    private val storeKey = stringPreferencesKey("user_theme_id")

    override suspend fun saveTheme(themeId: String) {
        saveLocally(themeId)
        saveToCloud(themeId)
    }

    override suspend fun getTheme(): String? {
        val localTheme = dataStore.data.first()[storeKey]
        if (localTheme != null) return localTheme

        val cloudTheme = getFromCloud()
        if (cloudTheme != null) saveLocally((cloudTheme))
        return cloudTheme
    }

    private suspend fun saveToCloud(themeId: String) {
        firestore.collection("settings")
            .document("theme")
            .set(mapOf("themeId" to themeId))
            .await()
    }
    private suspend fun getFromCloud(): String? {
        return try {
            firestore.collection("settings")
                .document("theme")
                .get().await().getString("themeId")
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveLocally(themeId: String) {
        dataStore.edit { prefs ->
            prefs[storeKey] = themeId
        }
    }
}