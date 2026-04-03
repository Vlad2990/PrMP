package com.example.app.domain.usecase

import com.example.app.domain.entities.HistoryItem
import com.example.app.domain.interfaces.HistoryRepositoryInterface

class GetHistoryUseCase(
    private val historyRepository: HistoryRepositoryInterface
) {
    suspend operator fun invoke(): List<HistoryItem> {
        return historyRepository.GetHistory()
    }
}