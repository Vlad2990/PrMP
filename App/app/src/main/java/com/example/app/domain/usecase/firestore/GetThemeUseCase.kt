package com.example.app.domain.usecase.firestore

import com.example.app.domain.interfaces.ThemeRepositoryInterface

class GetThemeUseCase(
    private val themeRepository: ThemeRepositoryInterface
) {
    suspend operator fun invoke(): String? {
        return themeRepository.getTheme()
    }
}