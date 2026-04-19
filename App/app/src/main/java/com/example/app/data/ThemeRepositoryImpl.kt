package com.example.app.data

import com.example.app.domain.interfaces.ThemeRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ThemeRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ThemeRepositoryInterface {

    private val userId: String? get() = auth.currentUser?.uid

    override suspend fun saveTheme(themeId: String) {
        userId?.let { uid ->
            saveToCloud(uid, themeId)
        }
    }

    override suspend fun getTheme(): String? {
        val uid = userId ?: return null
        return getFromCloud(uid)
    }

    private suspend fun saveToCloud(uid: String, themeId: String) {
        try {
            firestore.collection("users")
                .document(uid)
                .collection("settings")
                .document("appearance")
                .set(mapOf("themeId" to themeId))
                .await()
        } catch (e: Exception) {
        }
    }

    private suspend fun getFromCloud(uid: String): String? {
        return try {
            firestore.collection("users")
                .document(uid)
                .collection("settings")
                .document("appearance")
                .get()
                .await()
                .getString("themeId")
        } catch (e: Exception) {
            null
        }
    }
}