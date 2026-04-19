package com.example.app.domain.interfaces

import com.example.app.domain.entities.HistoryItem

interface HistoryRepositoryInterface {
    suspend fun getHistory(): List<HistoryItem>
    suspend fun saveToHistory(item: HistoryItem)
}