package com.example.app.domain.usecase.firestore

import com.example.app.domain.interfaces.ThemeRepositoryInterface

class SetThemeUseCase(private val themeRepository: ThemeRepositoryInterface) {
    suspend operator fun invoke(theme: String) {
        themeRepository.saveTheme(theme)
    }
}