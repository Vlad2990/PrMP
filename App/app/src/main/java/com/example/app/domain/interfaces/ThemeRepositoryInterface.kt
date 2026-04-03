package com.example.app.domain.interfaces

interface ThemeRepositoryInterface {
    suspend fun saveTheme(themeId: String)
    suspend fun getTheme(): String?
}