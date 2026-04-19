package com.example.app.domain.interfaces

interface UserAuthRepositoryInterface {
    suspend fun createUser(email: String, password: String) : Boolean
    suspend fun login(email: String, password: String) : Boolean
    suspend fun setupPassKey(pin: String) : Boolean
    suspend fun verifyPassKey(pin: String) : Boolean
    suspend fun isPassKeySet() : Boolean
    suspend fun isUserLoggedIn() : Boolean
}