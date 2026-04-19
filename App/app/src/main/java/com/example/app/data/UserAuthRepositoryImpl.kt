package com.example.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app.di.dataStore
import com.example.app.domain.interfaces.UserAuthRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UserAuthRepositoryImpl(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : UserAuthRepositoryInterface {

    override suspend fun createUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setupPassKey(pin: String): Boolean {
        if (pin.length != 6 || !pin.all { it.isDigit() }) return false
        val userId = firebaseAuth.currentUser?.uid

        if (userId?.isBlank() ?: true) return false

        val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
        val hash = hashPin(pin, salt)
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("passkey_hash_$userId")] = hash
            prefs[stringPreferencesKey("passkey_salt_$userId")] = salt.toHex()
            prefs[booleanPreferencesKey("is_passkey_set_$userId")] = true
        }
        return true
    }

    override suspend fun verifyPassKey(pin: String): Boolean {
        val userId = firebaseAuth.currentUser?.uid
        if (userId?.isBlank() ?: true) return false

        val prefs = context.dataStore.data.first()

        val hashKey = stringPreferencesKey("passkey_hash_$userId")
        val saltKey = stringPreferencesKey("passkey_salt_$userId")

        val storedHash = prefs[hashKey] ?: return false
        val saltHex = prefs[saltKey] ?: return false

        val salt = saltHex.hexToByteArray()
        val generatedHash = hashPin(pin, salt)

        return generatedHash == storedHash
    }

    override suspend fun isPassKeySet(): Boolean {
        val userId = firebaseAuth.currentUser?.uid
        if (userId?.isBlank() ?: true) return false
        val prefs = context.dataStore.data.first()
        return prefs[booleanPreferencesKey("is_passkey_set_$userId")] ?: false
    }
    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, 600_000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded.toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun String.hexToByteArray(): ByteArray =
        chunked(2) { it.toString().toInt(16).toByte() }.toByteArray()
}