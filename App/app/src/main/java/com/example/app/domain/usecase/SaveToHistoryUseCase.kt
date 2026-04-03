package com.example.app.domain.usecase

import com.example.app.domain.entities.HistoryItem
import com.example.app.domain.interfaces.HistoryRepositoryInterface

class SaveToHistoryUseCase(
    private val historyRepository: HistoryRepositoryInterface
) {
    suspend operator fun invoke(item: HistoryItem) {
        historyRepository.SaveToHistory(item)
    }
}