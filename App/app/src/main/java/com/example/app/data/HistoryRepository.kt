package com.example.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app.domain.entities.HistoryItem
import com.example.app.domain.interfaces.HistoryRepositoryInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class HistoryRepository(
    private val dataStore: DataStore<Preferences>,
    private val firestore: FirebaseFirestore
) : HistoryRepositoryInterface {

    private val historyKey = stringPreferencesKey("calculation_history")
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun SaveToHistory(item: HistoryItem) {
        saveLocally(item)
        saveToCloud(item)
    }

    override suspend fun GetHistory(): List<HistoryItem> {
        val localHistory = getFromLocal()
        if (localHistory.isNotEmpty()) {
            return localHistory
        }

        val cloudHistory = getFromCloud()
        if (cloudHistory.isNotEmpty()) {
            saveLocallyBatch(cloudHistory)
        }
        return cloudHistory
    }

    private suspend fun saveLocally(item: HistoryItem) {
        val currentList = getFromLocal().toMutableList()
        currentList.add(0, item)

        // Ограничиваем историю, например, 50 записями
        val limitedList = currentList.take(50)

        val jsonString = json.encodeToString(limitedList)

        dataStore.edit { prefs ->
            prefs[historyKey] = jsonString
        }
    }

    private suspend fun saveLocallyBatch(items: List<HistoryItem>) {
        val jsonString = json.encodeToString(items)

        dataStore.edit { prefs ->
            prefs[historyKey] = jsonString
        }
    }

    private suspend fun getFromLocal(): List<HistoryItem> {
        return try {
            val jsonString = dataStore.data.first()[historyKey] ?: return emptyList()
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun saveToCloud(item: HistoryItem) {
        try {
            firestore.collection("users")
                .document("history")
                .collection("calculations")
                .add(item)
                .await()

        } catch (e: Exception) {
        }
    }

    private suspend fun getFromCloud(): List<HistoryItem> {
        return try {
            val snapshot = firestore.collection("users")
                .document("history")
                .collection("calculations")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            snapshot.toObjects(HistoryItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}