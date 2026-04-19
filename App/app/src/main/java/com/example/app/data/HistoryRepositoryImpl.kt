package com.example.app.data

import android.util.Log
import com.example.app.domain.entities.HistoryItem
import com.example.app.domain.interfaces.HistoryRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class HistoryRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HistoryRepositoryInterface {

    private val userId: String? get() = auth.currentUser?.uid

    override suspend fun saveToHistory(item: HistoryItem) {
        userId?.let { uid ->
            saveToCloud(uid, item)
        }
    }

    override suspend fun getHistory(): List<HistoryItem> {
        val uid = userId ?: return emptyList()
        return getFromCloud(uid)
    }

    private suspend fun saveToCloud(uid: String, item: HistoryItem) {
        try {
            firestore.collection("users")
                .document(uid)
                .collection("calculations")
                .add(item)
                .await()
        } catch (e: Exception) {
        }
    }
    private suspend fun getFromCloud(uid: String): List<HistoryItem> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("calculations")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            snapshot.toObjects(HistoryItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}